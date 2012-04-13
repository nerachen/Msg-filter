package com.yfvesh.tm.enterprisemsg.smsobserver;
import android.net.Uri;
import android.provider.BaseColumns;

public interface SMS extends BaseColumns {

	Uri CONTENT_URI = Uri.parse("content://sms");

    String FILTER  = "startfilter";

    String TYPE = "type";

    String THREAD_ID = "thread_id";

    String ADDRESS = "address";

    String PERSON_ID = "person";

    String DATE = "date";

    String READ = "read";

    String BODY = "body";

    String PROTOCOL = "protocol";

 
    int MESSAGE_TYPE_ALL    = 0;

    int MESSAGE_TYPE_INBOX  = 1;

    int MESSAGE_TYPE_SENT   = 2;

    int MESSAGE_TYPE_DRAFT  = 3;

    int MESSAGE_TYPE_OUTBOX = 4;

    int MESSAGE_TYPE_FAILED = 5; // for failed outgoing messages

    int MESSAGE_TYPE_QUEUED = 6; // for messages to send later

    int PROTOCOL_SMS = 0;//SMS_PROTO

    int PROTOCOL_MMS = 1;//MMS_PROTO
}
