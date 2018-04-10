package eu.dkaratzas.starsgl.widget;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import eu.dkaratzas.starsgl.render.StarWarsRenderer;

public class StarWarsTilesGLSurfaceView extends GLSurfaceView {
    private StarWarsRenderer mRenderer;

    public StarWarsTilesGLSurfaceView(Context context) {
        super(context);
    }

    public StarWarsTilesGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onPause() {
        super.onPause();
        mRenderer.cancelAnimation();
    }

    public void setRenderer(StarWarsRenderer renderer) {
        super.setRenderer(renderer);
        mRenderer = renderer;
    }

}
