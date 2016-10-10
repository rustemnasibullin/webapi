package ru.mtt.webapi.mina;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoderFactory;
import org.apache.mina.filter.codec.demux.MessageDecoderAdapter;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;
import org.apache.mina.http.HttpServerDecoder;

/**
 *  Unified Meessage decoder
 * 
 *  @author rnasibullin@mtt.ru
 */

public class UnifiedMessageDecoder extends MessageDecoderAdapter {
    public UnifiedMessageDecoder() {
        super();
    }


    @Override
    public MessageDecoderResult decode(IoSession ioSession, IoBuffer ioBuffer,
                                       ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
        // TODO Implement this method
        HttpServerDecoder d = new HttpServerDecoder ();
        return null;
    }

    @Override
    public MessageDecoderResult decodable(IoSession ioSession, IoBuffer ioBuffer) {
        // TODO Implement this method
        return null;
    }


}
