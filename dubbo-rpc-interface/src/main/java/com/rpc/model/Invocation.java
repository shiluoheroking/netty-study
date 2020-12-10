package com.rpc.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author haikuan1
 */
@AllArgsConstructor
@Getter
@Setter
public class Invocation implements Serializable {

    private String className;

    private String methodName;

    private Class<?>[] parameterType;

    private Object[] parameterValue;

}
