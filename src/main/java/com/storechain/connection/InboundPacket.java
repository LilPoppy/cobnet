package com.storechain.connection;

import java.nio.charset.Charset;
import java.util.IllegalFormatException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.storechain.common.Endian;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class InboundPacket extends Packet {

	private ByteBuf byteBuf;
	
	private long opcode;
	
	public InboundPacket(byte[] data, Endian endian, Charset charset) {
		this(Unpooled.copiedBuffer(data), endian, charset);
	}
	
	
	public InboundPacket(ByteBuf buf, Endian endian, Charset charset) {
		super(buf.array(), endian, charset);
		this.byteBuf = buf.copy();
	}

    
    public InboundOperation getOperation() {
    	return InboundOperation.getByCode(opcode);
    }
    
    public long getOpcode() {
    	return this.opcode;
    }
    
    public void decodeHeader() {
    	
    	if(this.byteBuf.array().length < 4) {
    		
    		throw new IllegalArgumentException("Header size is less then 32 bits");
    	}
    	
    	this.opcode = decodeUInt();
    }
    
    public byte[] decode(long length) {
    	
    	ByteBuf buf = Unpooled.buffer();
    	
    	long left = length;
		
    	for(int i = 0; i < Math.ceil(length / (double)Integer.MAX_VALUE); i++) {
    		
    		buf.writeBytes(byteBuf.readBytes(left > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)left));
    		
    		left -= left > Integer.MAX_VALUE ? Integer.MAX_VALUE : left;
    	}
    	
    	return buf.array();
    }
    
    public byte decodeByte() {
        return byteBuf.readByte();
    }
    
    public short decodeUByte() {
        return byteBuf.readUnsignedByte();
    }
    
    public short decodeShort() {
    	if(this.endian == Endian.BIG) {
    		return byteBuf.readShort();
    	}
    	
    	return byteBuf.readShortLE();
    }
    
    public int decodeUShort() {
    	if(this.endian == Endian.BIG) {
    		return byteBuf.readUnsignedShort();
    	}
    	
    	return byteBuf.readUnsignedShortLE();
    }
    
    
    public int decodeInt() {
    	if(this.endian == Endian.BIG) {
    		return byteBuf.readInt();
    	}
    	
    	return byteBuf.readIntLE();
    }
    
    public long decodeUInt() {
    	if(this.endian == Endian.BIG) {
    		return byteBuf.readUnsignedInt();
    	}
    	
    	return byteBuf.readUnsignedIntLE();
    }
    
    public long decodeLong() {
    	if(this.endian == Endian.BIG) {
    		return byteBuf.readLong();
    	}
    	
    	return byteBuf.readLongLE();
    }
    
    public float decodeFloat() {
    	if(this.endian == Endian.BIG) {
    		return byteBuf.readFloat();
    	}
    	
    	return byteBuf.readFloatLE();
    }
    
    public double decodeDouble() {
    	if(this.endian == Endian.BIG) {
    		return byteBuf.readDouble();
    	}
    	
    	return byteBuf.readDoubleLE();
    }
    
    public String decodeString() {
    	long size = this.decodeUInt();
    	return new String(this.decode(size), this.charset);
    }
    
    public String decodeText() {
    	long size = this.decodeLong();
    	return new String(this.decode(size), this.charset);
    }
    
    @SuppressWarnings("rawtypes")
	public Map decodeMap() throws JsonMappingException, JsonProcessingException {
    	return new ObjectMapper().readValue(decodeString(), Map.class);
    }
    
    @SuppressWarnings("rawtypes")
	public Map decodeBigMap() throws JsonMappingException, JsonProcessingException {
    	return new ObjectMapper().readValue(decodeText(), Map.class);
    }

    @Override
    public int getLength() {
        return byteBuf.readableBytes();
    }
	
    @Override
    public byte[] getData() {
        return byteBuf.array();
    }
    
    @Override
    public void setData(byte[] buf) {
    	dispose();
    	super.setData(buf);
    	byteBuf = Unpooled.copiedBuffer(buf);
    }
    
	@Override
	public ByteBuf getBuf() {
		return byteBuf.asReadOnly();
	}

	@Override
	public InboundPacket clone() throws CloneNotSupportedException {
		return new InboundPacket(byteBuf, endian, charset);
	}

	@Override
	public void dispose() {
		byteBuf.release();
	}
}
