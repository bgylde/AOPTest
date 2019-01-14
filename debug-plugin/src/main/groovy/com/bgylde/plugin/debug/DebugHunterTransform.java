package com.bgylde.plugin.debug;

import com.android.build.api.transform.Context;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformOutputProvider;
import com.bgylde.plugin.debug.bytecode.DebugWeaver;
import com.bgylde.transform.HunterTransform;
import com.bgylde.transform.RunVariant;

import org.gradle.api.Project;

import java.io.IOException;
import java.util.Collection;

/**
 * Created by wangyan on 2019/1/12
 */
public class DebugHunterTransform extends HunterTransform {

    private static final String DEBUG_HUNTER_NAME = "debugHunterExt";
    private Project project;
    private DebugHunterExtension debugHunterExtension;

    public DebugHunterTransform(Project project) {
        super(project);
        this.project = project;
        project.getExtensions().create(DEBUG_HUNTER_NAME, DebugHunterExtension.class);
        this.bytecodeWeaver = new DebugWeaver();
    }

    @Override
    public void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        debugHunterExtension = (DebugHunterExtension) project.getExtensions().getByName(DEBUG_HUNTER_NAME);
        this.bytecodeWeaver.setExtension(debugHunterExtension);
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental);
    }

    @Override
    protected RunVariant getRunVariant() {
        return debugHunterExtension.runVariant;
    }

    @Override
    protected boolean inDuplcatedClassSafeMode() {
        return debugHunterExtension.duplcatedClassSafeMode;
    }
}
