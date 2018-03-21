package cc.fish.coreui.annotation;

import android.app.Fragment;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cc.fish.coreui.BaseFragment;

/**
 * Created by fish on 16-4-25.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Fragments {
    Class<? extends BaseFragment>[] value();
}
