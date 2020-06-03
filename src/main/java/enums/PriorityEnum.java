package enums;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PriorityEnum {
    INIT(0, "Init"),
    USER(1, "User"),
    SYSTEM(2, "System")
    ;

    private int code;
    private String message;
}
