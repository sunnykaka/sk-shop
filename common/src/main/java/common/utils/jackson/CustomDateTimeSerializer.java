package common.utils.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.ser.JacksonJodaFormat;
import com.fasterxml.jackson.datatype.joda.ser.JodaDateSerializerBase;
import common.utils.DateUtils;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;

/**
 * Created by liubin on 15-3-27.
 */
public class CustomDateTimeSerializer extends JodaDateSerializerBase<DateTime> {

    protected final static JacksonJodaFormat DEFAULT_FORMAT
            = new JacksonJodaFormat(ISODateTimeFormat.dateTime().withZoneUTC());


    public CustomDateTimeSerializer() {
        super(DateTime.class, DEFAULT_FORMAT);
    }

    @Override
    public JodaDateSerializerBase withFormat(JacksonJodaFormat format) {
        return new CustomDateTimeSerializer();
    }

    @Override
    public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (_useTimestamp(provider)) {
            jgen.writeNumber(value.getMillis());
        } else {
            jgen.writeString(DateUtils.printDateTime(value));
        }

    }

}
