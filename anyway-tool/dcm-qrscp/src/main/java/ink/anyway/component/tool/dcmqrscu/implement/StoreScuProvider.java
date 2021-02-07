package ink.anyway.component.tool.dcmqrscu.implement;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;

import ink.anyway.component.tool.dcmqrscu.pojo.FileToSend;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.dcm4che3.data.Tag;
import org.dcm4che3.data.UID;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.imageio.codec.Decompressor;
import org.dcm4che3.io.DicomInputStream;
import org.dcm4che3.io.SAXReader;
import org.dcm4che3.io.DicomInputStream.IncludeBulkData;
import org.dcm4che3.net.ApplicationEntity;
import org.dcm4che3.net.Association;
import org.dcm4che3.net.Connection;
import org.dcm4che3.net.DataWriterAdapter;
import org.dcm4che3.net.Device;
import org.dcm4che3.net.DimseRSPHandler;
import org.dcm4che3.net.InputStreamDataWriter;
import org.dcm4che3.net.Status;
import org.dcm4che3.net.pdu.AAssociateRQ;
import org.dcm4che3.net.pdu.PresentationContext;
import org.dcm4che3.tool.common.CLIUtils;
import org.dcm4che3.util.SafeClose;
import org.dcm4che3.util.TagUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

/**
 * @author Justin Li (lhbzjd@163.com)
 */
@Service
@ConditionalOnProperty(prefix = "store.scu", name = "bind")
public class StoreScuProvider {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public interface RSPHandlerFactory {

        DimseRSPHandler createDimseRSPHandler(Association association, File f);
    }

    private ResourceBundle rb = ResourceBundle
            .getBundle("org.dcm4che3.tool.storescu.messages");

    private ApplicationEntity ae;
    private Connection remote;
    private final AAssociateRQ rq = new AAssociateRQ();
    private final RelatedGeneralSOPClasses relSOPClasses = new RelatedGeneralSOPClasses();
    private Attributes attrs;
    private String uidSuffix;
    private boolean relExtNeg;
    private int priority;

    @Value("${store.scu.scp.ip-port-aet}")
    private String qrScpBind;

    @Value("${store.scu.bind}")
    private String qrScuBind;

    private long totalSize;
    private int filesSent;

    private String baseScuDirStr;

    private RSPHandlerFactory rspHandlerFactory = new RSPHandlerFactory() {

        @Override
        public DimseRSPHandler createDimseRSPHandler(final Association association, final File f) {

            return new DimseRSPHandler(association.nextMessageID()) {

                @Override
                public void onDimseRSP(Association as, Attributes cmd,
                        Attributes data) {
                    super.onDimseRSP(as, cmd, data);
                    StoreScuProvider.this.onCStoreRSP(cmd, f);
                }
            };
        }
    };

    public void setRspHandlerFactory(RSPHandlerFactory rspHandlerFactory) {
        this.rspHandlerFactory = rspHandlerFactory;
    }

    public AAssociateRQ getAAssociateRQ() {
        return rq;
    }

    public Connection getRemoteConnection() {
        return remote;
    }

    public Attributes getAttributes() {
        return attrs;
    }

    public void setAttributes(Attributes attrs) {
        this.attrs = attrs;
    }

    public final void setPriority(int priority) {
        this.priority = priority;
    }

    public final void setUIDSuffix(String uidSuffix) {
        this.uidSuffix = uidSuffix;
    }

    private CommandLine parseComandLine(String[] args)
            throws ParseException {
        Options opts = new Options();
        CLIUtils.addConnectOption(opts);
        CLIUtils.addBindOption(opts, "STORESCU");
        CLIUtils.addAEOptions(opts);
        CLIUtils.addResponseTimeoutOption(opts);
        CLIUtils.addPriorityOption(opts);
        CLIUtils.addCommonOptions(opts);
        addTmpFileOptions(opts);
        addRelatedSOPClassOptions(opts);
        addAttributesOption(opts);
        addUIDSuffixOption(opts);
        return CLIUtils.parseComandLine(args, opts, rb, StoreScuProvider.class);
    }

    @SuppressWarnings("static-access")
    private void addAttributesOption(Options opts) {
        opts.addOption(Option.builder("s").hasArgs().argName("[seq/]attr=value")
                .valueSeparator('=').desc(rb.getString("set"))
                .build());
    }

    @SuppressWarnings("static-access")
    public void addUIDSuffixOption(Options opts) {
        opts.addOption(Option.builder().hasArg().argName("suffix")
                .desc(rb.getString("uid-suffix"))
                .longOpt("uid-suffix").build());
    }

    @SuppressWarnings("static-access")
    public void addTmpFileOptions(Options opts) {
        opts.addOption(Option.builder().hasArg().argName("directory")
                .desc(rb.getString("tmp-file-dir"))
                .longOpt("tmp-file-dir").build());
        opts.addOption(Option.builder().hasArg().argName("prefix")
                .desc(rb.getString("tmp-file-prefix"))
                .longOpt("tmp-file-prefix").build());
        opts.addOption(Option.builder().hasArg().argName("suffix")
                .desc(rb.getString("tmp-file-suffix"))
                .longOpt("tmp-file-suffix").build());
    }

    @SuppressWarnings("static-access")
    private void addRelatedSOPClassOptions(Options opts) {
        opts.addOption(null, "rel-ext-neg", false, rb.getString("rel-ext-neg"));
        opts.addOption(Option.builder().hasArg().argName("file|url")
                .desc(rb.getString("rel-sop-classes"))
                .longOpt("rel-sop-classes").build());
    }

    @PostConstruct
    public void init(){

        this.baseScuDirStr = System.getProperty("user.dir")+"/to-store-scu";

        try {
            File baseScuDir = new File(baseScuDirStr);
            if(!baseScuDir.exists()||!baseScuDir.isDirectory()){
                baseScuDir.mkdirs();
            }
            CommandLine cl = parseComandLine(new String[]{"-b", qrScuBind, "-c", qrScpBind, this.baseScuDirStr});
            Device device = new Device("storescu");
            Connection conn = new Connection();
            device.addConnection(conn);
            ApplicationEntity ae = new ApplicationEntity("STORESCU");
            device.addApplicationEntity(ae);
            ae.addConnection(conn);

            this.remote = new Connection();
            this.ae = ae;
            rq.addPresentationContext(new PresentationContext(1,
                    UID.VerificationSOPClass, UID.ImplicitVRLittleEndian));

            CLIUtils.configureConnect(this.remote, this.rq, cl);
            CLIUtils.configureBind(conn, ae, cl);
            CLIUtils.configure(conn, cl);
            this.remote.setTlsProtocols(conn.getTlsProtocols());
            this.remote.setTlsCipherSuites(conn.getTlsCipherSuites());
            configureRelatedSOPClass(this, cl);
            this.setAttributes(new Attributes());
            CLIUtils.addAttributes(this.attrs, cl.getOptionValues("s"));
            this.setUIDSuffix(cl.getOptionValue("uid-suffix"));
            this.setPriority(CLIUtils.priorityOf(cl));

            ExecutorService executorService = Executors
                    .newSingleThreadExecutor();
            ScheduledExecutorService scheduledExecutorService = Executors
                    .newSingleThreadScheduledExecutor();
            device.setExecutor(executorService);
            device.setScheduledExecutor(scheduledExecutorService);
        } catch (Exception e) {
            logger.error(rb.getString("try"), e);
        }

    }

    public void configureRelatedSOPClass(StoreScuProvider storescu,
                                         CommandLine cl) throws IOException {
        if (cl.hasOption("rel-ext-neg")) {
            storescu.enableSOPClassRelationshipExtNeg(true);
            Properties p = new Properties();
            CLIUtils.loadProperties(
                    cl.hasOption("rel-sop-classes") ? cl
                            .getOptionValue("rel-ext-neg")
                            : "resource:rel-sop-classes.properties", p);
            storescu.relSOPClasses.init(p);
        }
    }

    public final void enableSOPClassRelationshipExtNeg(boolean enable) {
        relExtNeg = enable;
    }

    public void echo(Association association) throws IOException, InterruptedException {
        association.cecho().next();
    }

    private void send(Association association, final File f, long fmiEndPos, String cuid, String iuid,
            String filets) throws IOException, InterruptedException,
            ParserConfigurationException, SAXException {
        String ts = selectTransferSyntax(association, cuid, filets);

        if (f.getName().endsWith(".xml")) {
            Attributes parsedDicomFile = SAXReader.parse(new FileInputStream(f));
            if (CLIUtils.updateAttributes(parsedDicomFile, attrs, uidSuffix))
                iuid = parsedDicomFile.getString(Tag.SOPInstanceUID);
            if (!ts.equals(filets)) {
                Decompressor.decompress(parsedDicomFile, filets);
            }
            association.cstore(cuid, iuid, priority,
                    new DataWriterAdapter(parsedDicomFile), ts,
                    rspHandlerFactory.createDimseRSPHandler(association, f));
        } else {
            if (uidSuffix == null && attrs.isEmpty() && ts.equals(filets)) {
                FileInputStream in = new FileInputStream(f);
                try {
                    in.skip(fmiEndPos);
                    InputStreamDataWriter data = new InputStreamDataWriter(in);
                    association.cstore(cuid, iuid, priority, data, ts,
                            rspHandlerFactory.createDimseRSPHandler(association, f));
                }catch (Exception e){
                    logger.error("storeScu send file error", e);
                }finally {
                    SafeClose.close(in);
                }
            } else {
                DicomInputStream in = new DicomInputStream(f);
                try {
                    in.setIncludeBulkData(IncludeBulkData.URI);
                    Attributes data = in.readDataset(-1, -1);
                    if (CLIUtils.updateAttributes(data, attrs, uidSuffix))
                        iuid = data.getString(Tag.SOPInstanceUID);
                    if (!ts.equals(filets)) {
                        Decompressor.decompress(data, filets);
                    }
                    association.cstore(cuid, iuid, priority,
                            new DataWriterAdapter(data), ts,
                            rspHandlerFactory.createDimseRSPHandler(association, f));
                }catch (Exception e){
                    logger.error("storeScu send file error", e);
                } finally {
                    SafeClose.close(in);
                }
            }
        }
    }

    private String selectTransferSyntax(Association association, String cuid, String filets) {
        Set<String> tss = association.getTransferSyntaxesFor(cuid);
        if (tss.contains(filets))
            return filets;

        if (tss.contains(UID.ExplicitVRLittleEndian))
            return UID.ExplicitVRLittleEndian;

        return UID.ImplicitVRLittleEndian;
    }

    public void closeAssociation(Association association) {
        try{
            if (association != null) {
                if (association.isReadyForDataTransfer())
                    association.release();
                association.waitForSocketClose();
            }
        }catch (Exception e){
            logger.error("close scu Association failed !!!", e);
        }
    }

    public Association openAssociation() {
        Association association = null;
        try{
            association = ae.connect(remote, rq);
        }catch (Exception e){
            logger.error("open scu Association failed !!!", e);
        }
        return association;
    }

    private void onCStoreRSP(Attributes cmd, File f) {
        int status = cmd.getInt(Tag.Status, -1);
        switch (status) {
        case Status.Success:
            totalSize += f.length();
            ++filesSent;
            System.out.print('.');
            break;
        case Status.CoercionOfDataElements:
        case Status.ElementsDiscarded:
        case Status.DataSetDoesNotMatchSOPClassWarning:
            totalSize += f.length();
            ++filesSent;
            System.err.println(MessageFormat.format(rb.getString("warning"),
                    TagUtils.shortToHexString(status), f));
            System.err.println(cmd);
            break;
        default:
            System.out.print('E');
            System.err.println(MessageFormat.format(rb.getString("error"),
                    TagUtils.shortToHexString(status), f));
            System.err.println(cmd);
        }
    }

    public List<File> sendFiles(List<File> toSendFiles){
        List<File> succFiles = new ArrayList<>();
        if(toSendFiles == null||toSendFiles.size()<=0)
            return succFiles;

        List<FileToSend> fileToSends = new ArrayList<>();
        for(File mo:toSendFiles){
            if(mo!=null&&mo.exists()&&mo.isFile()){
                FileToSend fileToSend = preSendFile(mo);
                if(fileToSend!=null)
                    fileToSends.add(fileToSend);
            }
        }

        Association association = openAssociation();
        if(association==null)
            return succFiles;

        for(FileToSend mo:fileToSends){
            try {
                if(association.isReadyForDataTransfer()&&mo.getToSend()!=null&&mo.getToSend().exists()&&mo.getToSend().isFile()){
                    send(association, mo.getToSend(), mo.getDsPos(), mo.getCuid(), mo.getIuid(),
                            mo.getTs());
                    succFiles.add(mo.getToSend());
                }
            }catch (Exception e){
                logger.error("", e);
            }
        }

        try {
            association.waitForOutstandingRSP();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            this.closeAssociation(association);
        }

        return succFiles;
    }

    public FileToSend preSendFile(File preSend){
        DicomInputStream in = null;
        try {
            in = new DicomInputStream(preSend);
            in.setIncludeBulkData(IncludeBulkData.NO);
            Attributes fmi = in.readFileMetaInformation();
            Attributes ds = in.readDataset(-1, Tag.PixelData);
            if (fmi == null || !fmi.containsValue(Tag.TransferSyntaxUID)
                    || !fmi.containsValue(Tag.MediaStorageSOPClassUID)
                    || !fmi.containsValue(Tag.MediaStorageSOPInstanceUID))
                fmi = ds.createFileMetaInformation(in.getTransferSyntax());

            FileToSend fts = new FileToSend();
            fts.setIuid(fmi.getString(Tag.MediaStorageSOPInstanceUID));
            fts.setCuid(fmi.getString(Tag.MediaStorageSOPClassUID));
            fts.setTs(fmi.getString(Tag.TransferSyntaxUID));
            fts.setDsPos(in.getPosition());
            fts.setToSend(preSend);

            if (fts.getCuid() == null || fts.getIuid() == null)
                return null;

            if (rq.containsPresentationContextFor(fts.getCuid(), fts.getTs()))
                return fts;

            if (!rq.containsPresentationContextFor(fts.getCuid())) {
                if (relExtNeg)
                    rq.addCommonExtendedNegotiation(relSOPClasses
                            .getCommonExtendedNegotiation(fts.getCuid()));
                if (!fts.getTs().equals(UID.ExplicitVRLittleEndian))
                    rq.addPresentationContext(new PresentationContext(rq
                            .getNumberOfPresentationContexts() * 2 + 1, fts.getCuid(),
                            UID.ExplicitVRLittleEndian));
                if (!fts.getTs().equals(UID.ImplicitVRLittleEndian))
                    rq.addPresentationContext(new PresentationContext(rq
                            .getNumberOfPresentationContexts() * 2 + 1, fts.getCuid(),
                            UID.ImplicitVRLittleEndian));
            }
            rq.addPresentationContext(new PresentationContext(rq
                    .getNumberOfPresentationContexts() * 2 + 1, fts.getCuid(), fts.getTs()));

            return fts;
        } catch (Exception e) {
            System.out.println();
            System.out.println("Failed to scan file " + preSend + ": " + e.getMessage());
            e.printStackTrace(System.out);
        } finally {
            SafeClose.close(in);
        }
        return null;
    }
}
