package org.apereo.cas.util.function;

import com.google.common.base.Suppliers;
import lombok.val;
import org.jooq.lambda.fi.util.function.CheckedFunction;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link FunctionUtilsTests}.
 *
 * @author Misagh Moayyed
 * @since 6.2.0
 */
@Tag("Utility")
class FunctionUtilsTests {

    @Test
    void verifyDoIf0() {
        val result = new AtomicBoolean();
        FunctionUtils.doIf(true, input -> result.set(true), (Consumer<String>) s -> result.set(false)).accept("input");
        assertTrue(result.get());

        FunctionUtils.doIf(false, input -> result.set(true), (Consumer<String>) s -> result.set(false)).accept("input");
        assertFalse(result.get());
    }

    @Test
    void verifyDoIf1() {
        assertFalse(FunctionUtils.doIf(input -> {
                throw new IllegalArgumentException();
            },
            Suppliers.ofInstance(Boolean.TRUE), Suppliers.ofInstance(Boolean.FALSE)).apply(Void.class));
    }

    @Test
    void verifyDoIf2() {
        val trueFunction = new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                throw new IllegalArgumentException();
            }
        };
        assertFalse(FunctionUtils.doIf(true,
            trueFunction, Suppliers.ofInstance(Boolean.FALSE)).get());
    }

    @Test
    void verifyDoIf3() {
        assertThrows(IllegalArgumentException.class, () -> FunctionUtils.doIf(input -> {
                throw new IllegalArgumentException();
            },
            t -> null,
            (CheckedFunction<Object, Boolean>) t -> {
                throw new IllegalArgumentException();
            }).apply(Void.class));
    }

    @Test
    void verifyDoIfNull() {
        var supplier = FunctionUtils.doIfNotNull(new Object(), () -> {
            throw new IllegalArgumentException();
        }, Suppliers.ofInstance(Boolean.FALSE));
        assertFalse(supplier.get());

        supplier = FunctionUtils.doIfNotNull(null, Suppliers.ofInstance(Boolean.TRUE), Suppliers.ofInstance(Boolean.FALSE));
        assertFalse(supplier.get());
        supplier = FunctionUtils.doIfNotNull(new Object(), Suppliers.ofInstance(Boolean.TRUE), Suppliers.ofInstance(Boolean.FALSE));
        assertTrue(supplier.get());
    }

    @Test
    void verifyDoIfNull1() {
        assertDoesNotThrow(() -> FunctionUtils.doIfNotNull(Boolean.TRUE, result -> {
            throw new IllegalArgumentException();
        }));
        val supplier = FunctionUtils.doIfNull(null, () -> {
            throw new IllegalArgumentException();
        }, Suppliers.ofInstance(Boolean.FALSE));
        assertFalse(supplier.get());
        assertDoesNotThrow(() -> FunctionUtils.doIfNotNull(null, __ -> {
            throw new IllegalArgumentException();
        }));
    }

    @Test
    void verifyDoIfNull2() {
        assertTrue(FunctionUtils.doIfNull(new Object(), () -> {
            throw new IllegalArgumentException();
        }, Suppliers.ofInstance(Boolean.TRUE)).get());
    }

    @Test
    void verifyDoAndHandle() {
        assertThrows(IllegalArgumentException.class,
            () -> FunctionUtils.doAndHandle((CheckedFunction<Object, Boolean>) o -> {
                throw new IllegalArgumentException();
            }, o -> {
                throw new IllegalArgumentException();
            }).apply(Void.class));

        assertFalse(FunctionUtils.doAndHandle((CheckedFunction<Object, Boolean>) o -> {
            throw new IllegalArgumentException();
        }, o -> false).apply(Void.class));
    }

    @Test
    void verifyDoAndHandle2() {
        var supplier = FunctionUtils.doAndHandle(
            () -> {
                throw new IllegalArgumentException();
            }, o -> {
                throw new IllegalArgumentException();
            });
        assertThrows(IllegalArgumentException.class, supplier::get);
        supplier = FunctionUtils.doAndHandle(
            () -> {
                throw new IllegalArgumentException();
            }, o -> false);
        assertFalse((Boolean) supplier.get());
    }

    @Test
    void verifyDoWithoutThrows() {
        val supplier = FunctionUtils.doWithoutThrows(o -> {
            throw new IllegalArgumentException();
        });
        assertFalse(supplier);
    }
}
