package org.apereo.cas.memcached.kryo.serial;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import lombok.val;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * This is {@link ImmutableNativeJavaListSerializer}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
public class ImmutableNativeJavaListSerializer extends Serializer<List<Object>> {
    public ImmutableNativeJavaListSerializer() {
        super(false, true);
    }

    @Override
    public void write(final Kryo kryo, final Output output, final List<Object> object) {
        output.writeInt(object.size(), true);
        for (final val elm : object) {
            kryo.writeClassAndObject(output, elm);
        }
    }

    @Override
    public List<Object> read(final Kryo kryo, final Input input, final Class<? extends List<Object>> aClass) {
        val size = input.readInt(true);
        val list = IntStream.range(0, size).mapToObj(i -> kryo.readClassAndObject(input)).toArray();
        return Arrays.stream(list).collect(Collectors.toList());
    }
}
