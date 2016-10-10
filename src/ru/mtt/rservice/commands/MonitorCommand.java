package ru.mtt.rservice.commands;

import ru.mtt.rservice.core.MAPIServiceFarmHandler;
import ru.mtt.rservice.mina.RServiceController;

/**
 *  Cronicle based command for retreive information about service farm statistics and OSS availability status
 * 
 *  @author rnasibullin@mtt.ru  Chief 
 */

public class MonitorCommand {
    
    RServiceController owner;

    public void setOwner(RServiceController owner) {
        this.owner = owner;
    }

    public RServiceController getOwner() {
        return owner;
    }

    public MonitorCommand() {
        super();
    }
    
    
    public void execute() {
        
           MAPIServiceFarmHandler serviceFarm = MAPIServiceFarmHandler.getInstance();
           serviceFarm.uploadActiveStatistics();
        
    }
    
    
    
}
