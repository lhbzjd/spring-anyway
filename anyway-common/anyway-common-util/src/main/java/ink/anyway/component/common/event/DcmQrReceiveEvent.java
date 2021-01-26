package ink.anyway.component.common.event;

import ink.anyway.component.common.pojo.DicomInfo;

public class DcmQrReceiveEvent {

    private String eventId;

    private long eventStartTimestamp;

    private DicomInfo info;

    public DcmQrReceiveEvent() {
    }

    public DcmQrReceiveEvent(String eventId, long eventStartTimestamp, DicomInfo info) {
        this.eventId = eventId;
        this.eventStartTimestamp = eventStartTimestamp;
        this.info = info;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public long getEventStartTimestamp() {
        return eventStartTimestamp;
    }

    public void setEventStartTimestamp(long eventStartTimestamp) {
        this.eventStartTimestamp = eventStartTimestamp;
    }

    public DicomInfo getInfo() {
        return info;
    }

    public void setInfo(DicomInfo info) {
        this.info = info;
    }
}
