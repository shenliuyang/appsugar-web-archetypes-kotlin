package org.appsugar.archetypes.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author shenliuyang
 * @version 1.0.0
 * @package org.appsugar.archetypes.domain.dto
 * @className Response
 * @date 2021-07-07  17:03
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response<T> {
    private Object result = null;
    //0正常,其他异常
    private int code = 0;
    private String msg = "success";
}
