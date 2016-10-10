package ru.mtt.webapi.core;

public interface IProcessorFactory {
       public IProcessor createProcessor(String stereotype);
}
