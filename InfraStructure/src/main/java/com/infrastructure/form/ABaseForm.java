package com.infrastructure.form;

import com.infrastructure.basedata.CodeException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;

/*
 *  @Created 16/06/2022
 *  @Project userservice
 *  @Author  kiumars khodadi
 */
@AllArgsConstructor
@Getter
@Setter
public abstract class ABaseForm implements Serializable {
    @Builder.Default
    private ArrayList<CodeException> errors = new ArrayList<>();
    @Builder.Default
    private String message  = "";
    @Builder.Default
    private boolean success = true;
    @Builder.Default
    private boolean nextPage = false;

    ABaseForm(){
        setSuccess(true);
        setNextPage(false);
        setMessage("");
    }

    public void addError(ABaseForm baseForm){
        getErrors().addAll(baseForm.getErrors());
        setSuccess(isSuccess() && baseForm.isSuccess());
        setMessage(getMessage().concat(baseForm.getMessage()));
    }
}
