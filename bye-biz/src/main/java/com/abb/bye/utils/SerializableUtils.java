package com.abb.bye.utils;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

import java.io.*;

/**
 * @author cenpeng.lwm
 * @since 2018/9/27
 */
public class SerializableUtils {
    public static byte[] serializable(Object obj) throws IOException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(bs);
        os.writeObject(obj);
        return bs.toByteArray();
    }

    public static Object deserializable(byte[] data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(byteArrayInputStream);
        return is.readObject();
    }

    public static byte[] hessian2serialize(Object obj) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        HessianOutput ho = new HessianOutput(os);
        ho.writeObject(obj);
        return os.toByteArray();
    }

    public static Object hessian2deserialize(byte[] by) throws IOException {
        ByteArrayInputStream is = new ByteArrayInputStream(by);
        HessianInput hi = new HessianInput(is);
        return hi.readObject();
    }
}
