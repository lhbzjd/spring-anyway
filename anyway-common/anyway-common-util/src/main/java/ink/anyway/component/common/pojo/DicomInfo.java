package ink.anyway.component.common.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class DicomInfo {

    public static final String SERIES_UID_UNKNOWN = "unknown";

    private String seriesInstanceUid;
    private String accessionNumber;
    private String modality;
    private String studyInstanceUid;
    private String patientAge;
    private Date patientBirthDate;
    private String patientId;
    private String patientName;
    private String patientSex;
    private String studyDescription;
    private String studyId;
    private Date studyTime;
    private String seriesDescription;
    private int seriesNumber;
    private Date acquisitionDateTime;
    private String imageDataTypeSequence;
    private int instanceNumber;
    private String instanceUid;
    private String localFilePath;

    private Boolean haveHandle;

}
