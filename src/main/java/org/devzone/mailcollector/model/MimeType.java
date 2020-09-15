package org.devzone.mailcollector.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum MimeType {
            VND_ACCESS("APPLICATION/vndms-access"),
            MS_ACCESS("application/msaccess"),
            OO_WORD("application/vnd.openxmlformats-officedocument.wordprocessingml.document"),
            OO_WORD_2("application/vndopenxmlformats-officedocumentwordprocessingmldocument"),
            OO_EXCEL("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
            OO_TEXT("application/vnd.oasis.opendocument.text"),
            OO_TEXT_2("application/vndoasisopendocumenttext"),
            ALL_TYPES_HACK("application/force-download"),
            ALL_TYPES("application/download"),
            VND_EXCEL("application/vnd.ms-excel"),
            VND_EXCEL_2("application/vndms-excel"),
            VND_EXCEL_3("application/vndoasisopendocumentspreadsheet"),
            VND_MISC("application/vndopenxmlformats"),
            OCTET_STREAM("application/octet-stream"),
            IMAGE_JPEG("image/jpeg"),
            PDF("application/pdf"),
            MS_WORD("application/msword"),
            MS_EXCEL("application/vnd.ms-excel");


    private String mimeType;

    MimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static List<String> getAllMimeTypes() {
        return Arrays.stream(MimeType.values()).map(m -> m.getMimeType()).collect(Collectors.toList());
    }
}
