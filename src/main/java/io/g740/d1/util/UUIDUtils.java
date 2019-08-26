package io.g740.d1.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

/**
 * @author : zxiuwu
 * @version : V1.0
 * @function :
 * @date : 2019/8/26 16:23
 * @description :
 */
public class UUIDUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(UUIDUtils.class);

    public static String compress() {
        UUID uuid = UUID.randomUUID();
        ByteBuffer bb = ByteBuffer.allocate(Long.BYTES * 2);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        byte[] array = bb.array();
        return Base64.getEncoder().encodeToString(array);
    }

    public static UUID decompress(String compressUUID) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(Base64.getDecoder().decode(compressUUID));
        return new UUID(byteBuffer.getLong(), byteBuffer.getLong());
    }


}
