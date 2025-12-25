package com.infrastructure.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
@AllArgsConstructor
@Getter
@Setter
public class OutputAPIForm <T> extends ABaseForm {

    private T data;
    public OutputAPIForm() {
//        setErrors(new ArrayList<>());
        setSuccess(true);
        setMessage("");

    }
}
