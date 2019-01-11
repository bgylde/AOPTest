package com.sohu.plugin.okhttp;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformOutputProvider;
import com.sohu.plugin.okhttp.bytecode.OkhttpWeaver;
import com.sohu.transform.HunterTransform;
import com.sohu.transform.RunVariant;

import org.gradle.api.Project;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by wangyan on 2019/1/11
 */
public class OkhttpHunterTransform extends HunterTransform {

    private static final String PROJECT_NAME = "okHttpHunterExt";
    private Project project;
    private OkhttpHunterExtension extension;

    public OkhttpHunterTransform(Project project) {
        super(project);
        this.project = project;
        project.getExtensions().create(PROJECT_NAME, OkhttpHunterExtension.class);
        this.bytecodeWeaver = new OkhttpWeaver();
    }

    @Override
    public void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        extension = (OkhttpHunterExtension) project.getExtensions().getByName(PROJECT_NAME);
        this.bytecodeWeaver.setExtension(extension);
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental);
    }

    @Override
    protected RunVariant getRunVariant() {
        return extension.runVariant;
    }

    @Override
    protected boolean inDuplcatedClassSafeMode() {
        return extension.duplcatedClassSafeMode;
    }
}
