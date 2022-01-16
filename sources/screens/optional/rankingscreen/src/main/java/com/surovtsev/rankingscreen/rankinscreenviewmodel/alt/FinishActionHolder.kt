package com.surovtsev.rankingscreen.rankinscreenviewmodel.alt

typealias FinishAction = () -> Unit

interface FinishActionHolder {
    var finishAction: FinishAction?
}

class FinishActionHolderImp: FinishActionHolder {
    override var finishAction: FinishAction? = null
}
