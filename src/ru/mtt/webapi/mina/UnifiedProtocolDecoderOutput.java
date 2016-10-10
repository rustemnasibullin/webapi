package ru.mtt.webapi.mina;

import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
/**
 *  Unified Decoder Output implementation
 * 
 *  @author rnasibullin@mtt.ru
 */
public class UnifiedProtocolDecoderOutput implements ProtocolDecoderOutput {
    public UnifiedProtocolDecoderOutput() {
        super();
    }


    @Override
    public void flush(IoFilter.NextFilter nextFilter, IoSession ioSession) {
        // TODO Implement this method

    }

    @Override
    public void write(Object object) {
           System.out.println (object.getClass().getName() + " - " + object);
    }


}
