package ru.mtt.webapi.core;

import ru.mtt.webapi.controller.XWebApiController;

/**
 *
 * Abstract (Unified) data access object
 * Concerns with distinct data channels
 *
 *
 * @author rnasibullin@mtt.ru
 */
public abstract class XDAOController extends XConfigurableObject {
    
    protected XWebApiController owner = null;

    public void setOwner(XWebApiController owner) {
        this.owner = owner;
    }

    public XWebApiController getOwner() {
        return owner;
    }

    
    public XDAOController() {
        super();
    }

   
}
