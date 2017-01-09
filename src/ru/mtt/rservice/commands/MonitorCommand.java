package ru.mtt.rservice.commands;

import java.util.Date;

import ru.mtt.rservice.core.MAPIServiceFarmHandler;
import ru.mtt.rservice.mina.RServiceController;
import ru.mtt.webapi.utils.XUtils;

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
        
           XUtils.ilog ("log/monitorcommand.log", "uploadActiveStatistics() "+(new Date()));
           MAPIServiceFarmHandler serviceFarm = MAPIServiceFarmHandler.getInstance();
           serviceFarm.uploadActiveStatistics();
        
    }

    
}
