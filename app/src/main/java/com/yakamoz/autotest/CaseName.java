package com.yakamoz.autotest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yakamoz on 15-11-14.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CaseName {

    String value();

}