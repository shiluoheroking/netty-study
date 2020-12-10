import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 用于封装消费者发送给服务提供者的接口参数信息
 */
@AllArgsConstructor
@Setter
@Getter
public class Invocation implements Serializable {

    /**
     * 想要调用的类名
     */
    private String className;

    /**
     * 想要调用的方法名
     */
    private String methodName;

    /**
     * 请求调用的参数类型
     */
    private Class<?>[] paramTypes;

    /**
     * 请求调用的参数值
     */
    private Object[] paramValues;
}
