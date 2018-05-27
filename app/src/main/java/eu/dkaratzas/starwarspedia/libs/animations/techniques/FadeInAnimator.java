/*
 * Copyright 2018 Dionysios Karatzas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.dkaratzas.starwarspedia.libs.animations.techniques;

import android.animation.ObjectAnimator;
import android.view.View;

import eu.dkaratzas.starwarspedia.libs.animations.BaseViewAnimator;

public class FadeInAnimator extends BaseViewAnimator {
    @Override
    public void prepare(View target) {
        target.setVisibility(View.VISIBLE);
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "alpha", 0, 1)
        );
    }
}