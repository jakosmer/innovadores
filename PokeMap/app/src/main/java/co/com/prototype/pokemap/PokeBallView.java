package co.com.prototype.pokemap;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Calendar;

/**
 * Created by carlviar on 2016/08/23.
 */
public class PokeBallView extends LinearLayout {
    private static ImageView imageButton;
    private AnimatorSet animator;

    public PokeBallView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.poke_ball_view, this);

        imageButton = (ImageView)findViewById(R.id.iv_pokeball);

        configureAnimation();
    }


    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if(animator == null){
            return;
        }

        if(visibility == VISIBLE){
            animator.start();
        }else{
            animator.end();
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        animator.end();
        animator = null;
        imageButton = null;
    }

    private void configureAnimation(){

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(imageButton, "x", 20f, 60f);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(imageButton, "x", 60f, 0f);
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(imageButton, "x", 0f, 20f);
        ObjectAnimator anim4 = ObjectAnimator.ofFloat(imageButton, "x", 20f, 40f);
        ObjectAnimator anim5 = ObjectAnimator.ofFloat(imageButton, "x", 40f, 0f);
        ObjectAnimator anim6 = ObjectAnimator.ofFloat(imageButton, "x", 0f, 60f);

        animator = new AnimatorSet();
        animator.play(anim1).after(anim2);
        animator.play(anim3).after(anim2);
        animator.play(anim4).after(anim3);
        animator.play(anim5).after(anim4);
        animator.play(anim6).after(anim5);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(300);

        animator.addListener(new Animator.AnimatorListener() {
            int count = 0;
            boolean justDelayed = false;

            @Override
            public void onAnimationStart(Animator animation) {

                if(!justDelayed) {
                    imageButton.setImageResource(R.drawable.poke_ball_active256x256);
                }

                justDelayed = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(animator == null){
                    return;
                }

                count++;
                long startDelay = 0;

                if(count%5 == 0){
                    startDelay = 3000;
                    justDelayed = true;

                    imageButton.setImageResource(R.drawable.poke_ball256x256);
                }

                animator.setStartDelay(startDelay);
                animator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                imageButton.setImageResource(R.drawable.poke_ball256x256);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                Log.i("PGO-ANIMATION", "repeat at: " + Calendar.getInstance().getTime().toString());
            }
        });

    }
}
