package eu.dkaratzas.starwarspedia.libs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;

public class FixedAppBarLayoutBehavior extends AppBarLayout.Behavior {

    public FixedAppBarLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDragCallback(new DragCallback() {
            @Override
            public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                return false;
            }
        });
    }
}