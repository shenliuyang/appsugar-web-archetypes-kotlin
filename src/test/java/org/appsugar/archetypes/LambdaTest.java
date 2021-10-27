package org.appsugar.archetypes;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
public class LambdaTest {

    public String getName() {
        return "xxx";
    }

    @Test
    public void testLambdaInstance() {
        brige();
        brige();
    }

    public void brige() {
      
        receiveLambdaInstance(LambdaTest::getName, LambdaTest::getName);
    }

    public <T, R> void receiveLambdaInstance(SFunction<T, R> lambda, SFunction<T, R> b) {
        log.debug("xxx class {} b class {}", lambda.hashCode(), b.hashCode());
        try {
            log.debug("method is {}", Arrays.asList(lambda.getClass().getDeclaredMethods()));
            Method m = lambda.getClass().getDeclaredMethod("writeReplace");
            m.setAccessible(true);
            SerializedLambda sl = (SerializedLambda) m.invoke(lambda);
            String xx = sl.getImplMethodName();
            log.debug("name is {}", xx);
        } catch (SecurityException | ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }


}
