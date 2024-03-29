package com.surovtsev.utils.compose.navigationanimationhelper

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry


@ExperimentalAnimationApi
class SimpleNavigationAnimationHelper(
    private val offsetX: Int,
    private val offsetY: Int,
    animationDuration: Int = 500
) {
    val fadingTween = tween<Float>(
        animationDuration
    )
    val slidingTween = tween<IntOffset>(
        animationDuration
    )

    val enterSliding = EnterSliding()
    val exitSliding = ExitSliding()

    inner class ConcreteEnterSliding {
        val fromTop = enterSliding.vertically(-offsetY)
        val fromBottom = enterSliding.vertically(offsetY)

        val fromLeft = enterSliding.horizontally(-offsetX)
        val fromRight = enterSliding.horizontally(offsetX)
    }

    inner class ConcreteExitSliding {
        val toTop = exitSliding.vertically(-offsetY)
        val toBottom = exitSliding.vertically(offsetY)

        val toLeft = exitSliding.horizontally(-offsetX)
        val toRight = exitSliding.horizontally(offsetX)
    }

    val concreteEnterSliding = ConcreteEnterSliding()
    val concreteExitSliding = ConcreteExitSliding()

    inner class EnterSliding {
        fun horizontally(initialOffsetX: Int) =
            { _: AnimatedContentTransitionScope<NavBackStackEntry> ->
                slideInHorizontally(
                    initialOffsetX = { initialOffsetX },
                    animationSpec = slidingTween
                )
            }

        fun vertically(initialOffsetY: Int) =
            { _: AnimatedContentTransitionScope<NavBackStackEntry> ->
                slideInVertically(
                    initialOffsetY = { initialOffsetY },
                    animationSpec = slidingTween
                )
            }
    }

    inner class ExitSliding {
        fun horizontally(targetOffsetX: Int) =
            { _: AnimatedContentTransitionScope<NavBackStackEntry> ->
                slideOutHorizontally(
                    targetOffsetX = { targetOffsetX },
                    animationSpec = slidingTween
                )
            }

        fun vertically(targetOffsetY: Int) =
            { _: AnimatedContentTransitionScope<NavBackStackEntry> ->
                slideOutVertically(
                    targetOffsetY = { targetOffsetY },
                    animationSpec = slidingTween
                )
            }
    }
}
