package ink.anyway.component.common.util;

import com.google.common.math.IntMath;
import ink.anyway.component.common.pojo.DicomInfo;
import org.dcm4che3.data.Attributes;
import org.dcm4che3.data.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DicomUtil {

    private static final Logger logger = LoggerFactory.getLogger(DicomUtil.class);

    public static void loadDicomInfo(DicomInfo info, Attributes attrs){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatD = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatT = new SimpleDateFormat("HH:mm:ss");

        info.setSeriesInstanceUid(attrs.getString(Tag.SeriesInstanceUID));
        if(!StringUtil.isValid(info.getSeriesInstanceUid()))
            info.setSeriesInstanceUid(DicomInfo.SERIES_UID_UNKNOWN);

        info.setAccessionNumber(attrs.getString(Tag.AccessionNumber));
        info.setModality(attrs.getString(Tag.Modality));
        info.setStudyInstanceUid(attrs.getString(Tag.StudyInstanceUID));
        info.setPatientAge(attrs.getString(Tag.PatientAge));
        try{
            info.setPatientBirthDate(attrs.getDate(Tag.PatientBirthDate));
        }catch (Exception e){
            logger.warn("PatientBirthDate format error", e);
        }
        info.setPatientId(attrs.getString(Tag.PatientID));
        info.setPatientName(attrs.getString(Tag.PatientName));
        info.setPatientSex(attrs.getString(Tag.PatientSex));

        info.setStudyDescription(attrs.getString(Tag.StudyDescription));
        info.setStudyId(attrs.getString(Tag.StudyID));
        try {
            Date studyDate = attrs.getDate(Tag.StudyDate);
            Date studyTime = attrs.getDate(Tag.StudyTime);
            info.setStudyTime(format.parse(formatD.format(studyDate)+" "+formatT.format(studyTime)));
        } catch (Exception e) {
            e.printStackTrace();
        }

        info.setSeriesDescription(attrs.getString(Tag.SeriesDescription));
        info.setSeriesNumber(attrs.getInt(Tag.SeriesNumber, 1));

        info.setAcquisitionDateTime(attrs.getDate(Tag.AcquisitionDateTime));
        info.setImageDataTypeSequence(attrs.getString(Tag.ImageDataTypeSequence));
        info.setInstanceNumber(attrs.getInt(Tag.InstanceNumber, 1));
        info.setInstanceUid(attrs.getString(Tag.SOPInstanceUID));
    }

    public static void main(String[] args){

//        System.out.println(getLongest2("aa aa aaaa aaa a a a aa aa a"));

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        System.out.println(sdf.format(new Date()));

        System.out.println(pow(5, 5));

        System.out.println(pow(5, 5));

        System.out.println(IntMath.pow(5,5));

        System.out.println(forPow(5, 5));

    }

    public static int getLongest(String input){
        int res = 0;
        if(input==null||"".equals(input.trim()))
            return res;


        String[] arr = input.trim().split(" ");
        for(String mo:arr){
            if(mo.trim().length()>res)
                res = mo.trim().length();
        }
        return res;
    }

    public static int getLongest2(String input){
        int res = 0;
        if(input==null||"".equals(input.trim()))
            return res;

        int start = 0;
        int end = input.indexOf(" ", start);
        while(end>=0){
            if(end-start>res)
                res = end-start;
            start = end;
            end = input.indexOf(" ", start);
        }
        return res;
    }

    public static double pow(double x, int y){
        double res = 0;
        System.out.println(System.currentTimeMillis());
        BigDecimal xb = new BigDecimal(x);
        res = xb.pow(y).doubleValue();
        System.out.println(System.currentTimeMillis());
        return res;
    }

    public static int forPow(int x, int y){
        int res = 1;
        System.out.println(System.currentTimeMillis());
        for(int i=0;i<y;i++){
            res = res * x;
        }
        System.out.println(System.currentTimeMillis());
        return res;
    }

    public static int bitPow(int n){
        int A = 33;
        for(int accum = 1;;n >>= 1){
            switch (n){
                case 0:
                    return accum;
                case 1:
                    return n*accum;
                default:
                    accum *= ((n&1)==0)?1:A;
                    A*=A;
            }
        }
//        return A;
    }
}
