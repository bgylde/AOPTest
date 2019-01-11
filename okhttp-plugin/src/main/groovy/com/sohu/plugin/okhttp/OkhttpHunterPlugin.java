package com.sohu.plugin.okhttp;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.Collections;

/**
 * Created by wangyan on 2019/1/11
 */
public final class OkhttpHunterPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        AppExtension appExtension = (AppExtension) project.getProperties().get("android");
        appExtension.registerTransform(new OkhttpHunterTransform(project), Collections.EMPTY_LIST);
    }
}
