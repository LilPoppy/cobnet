package com.storechain.connection;

import java.nio.charset.Charset;
import java.util.Map;

import org.bouncycastle.util.Arrays;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.storechain.Endian;
import com.storechain.connection.netty.NettyServer;
import com.storechain.constants.LogicConstants;
import com.storechain.interfaces.connection.Encodable;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;

public class OutboundPacket extends Packet {
	
	private long opcode;
	
	private ByteBuf byteBuf;
	
    public OutboundPacket(NettyServer server, long opcode) {
        this(server);
        encodeUInt(opcode);
        this.opcode = opcode;
    }

	public OutboundPacket(byte[] data, Endian endian, Charset charset) {
		super(data, endian, charset);
        this.byteBuf = ByteBufAllocator.DEFAULT.buffer().clear();
        encode(data);
        ByteBuf buf = Unpooled.buffer();
        buf.writeBytes(Arrays.copyOfRange(data, 0, 4));
        if(this.endian == Endian.BIG) {
        	this.opcode = buf.readUnsignedInt();
        } else {
        	this.opcode = buf.readUnsignedIntLE();
        }
        buf.release();
	}
	
    public OutboundPacket(NettyServer server) {
        this(new byte[]{}, server.getConfiguration().getEncodeEndian(), server.getConfiguration().getCharset());
    }
    
    public OutboundOperation getOperation() {
    	return OutboundOperation.getByCode(opcode);
    }
    
    public long getOpcode() {
    	return this.opcode;
    }
    
    public void encode(byte[] data) {
    	byteBuf.writeBytes(data);
    }
    
    public void encode(Encodable encodable) {
        encodable.encode(this);
    }
    
    public void encodeByte(byte b) {
    	byteBuf.writeByte(b);
    }
    
    public void encodeByte(int b) {
    	encodeByte((byte)b);
    }
    
    public void encodeBoolean(boolean b) {
    	byteBuf.writeBoolean(b);
    }
    
    public void encodeShort(short value) {
    	if(this.endian == Endian.BIG) {
    		byteBuf.writeShort(value);
    	} else {
    		byteBuf.writeShortLE(value);
    	}
    }
    
    public void encodeUShort(int value) {
		encodeShort((short)(value & 0xFFFF));
    }
    
    public void encodeInt(int value) {
    	if(this.endian == Endian.BIG) {
    		byteBuf.writeInt(value);
    	} else {
    		byteBuf.writeIntLE(value);
    	}
    }
    
    public void encodeUInt(long value) {
    	encodeInt((int)(value & 0xFFFFFFFFL));
    }
    
    public void encodeLong(long value) {
    	if(this.endian == Endian.BIG) {
    		byteBuf.writeLong(value);
    	} else {
    		byteBuf.writeLongLE(value);
    	}
    }
    
    public void encodeString(String value) {
    	byte[] bs = value.getBytes(charset);
    	
    	if(bs.length > LogicConstants.MAX_UINT_VALUE) {
    		encodeLong(bs.length);
    	} else {
    		encodeUInt(bs.length);
    	}
    	
    	encode(bs);
    }
    
    @SuppressWarnings("rawtypes")
	public void encodeMap(Map map) throws JsonProcessingException {
    	encodeString(new ObjectMapper().writeValueAsString(map));
    }
    
	
    @Override
    public void setData(byte[] data) {
        super.setData(data);
        byteBuf.clear();
        encode(data);
    }

    @Override
    public byte[] getData() {

        if (byteBuf.hasArray()) {
            return byteBuf.array();
        } else {
            byte[] arr = new byte[byteBuf.writerIndex()];
            byteBuf.nioBuffer().get(arr, 0, byteBuf.writerIndex());
            return arr;
        }
    }
    
	@Override
	public ByteBuf getBuf() {
		return byteBuf.asReadOnly();
	}
	
	@Override
	public void dispose() {
		byteBuf.clear();
	}

	@Override
	public Packet clone() throws CloneNotSupportedException {
		return new OutboundPacket(this.getData(), endian, charset);
	}
	
    @Override
    protected void finalize() throws Throwable {
    	dispose();
    }


}
