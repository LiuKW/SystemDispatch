package enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusEnum {

    WAIT(0, "WAIT"),
    BLOCK(1, "BLOCK"),
    READY(2, "READY"),
    RUNNING(3, "RUNNING")
    ;

    private int code;
    private String message;


}
