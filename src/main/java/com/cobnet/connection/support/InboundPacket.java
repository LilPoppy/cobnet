package com.cobnet.connection.support;

import com.cobnet.common.Endian;
import com.cobnet.interfaces.connection.InputTransmission;
import com.cobnet.spring.boot.controller.handler.InboundOperation;
import com.cobnet.spring.boot.core.ProjectBeanHolder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Arrays;
import java.util.Map;

public class InboundPacket extends Packet implements InputTransmission<byte[]> {

	private ByteBuf byteBuf;
	
	private long opcode;

	public InboundPacket(NettyServer<?> server) {

		this(new byte[]{}, server);
	}
	
	public InboundPacket(byte[] data, NettyServer<?> server) {

		this(Unpooled.copiedBuffer(data), server);
	}

	public InboundPacket(ByteBuf buf, NettyServer<?> server) {

		super(buf.array(), server);

		System.out.println(buf.readableBytes() + ":" + Arrays.toString(buf.array()));
		this.byteBuf = buf;

		if(buf.readableBytes() >= 4) {

			this.decodeHeader();
		}
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
    
    public byte[] decode(int length) {

		byte[] bs = new byte[length];

		this.byteBuf.readBytes(bs);

    	return bs;
    }
    
    public byte decodeByte() {

		return byteBuf.readByte();
    }
    
    public short decodeUByte() {

		return byteBuf.readUnsignedByte();
    }
    
    public short decodeShort() {

    	if(this.getServer().getEndian() == Endian.BIG) {

    		return byteBuf.readShort();
    	}
    	
    	return byteBuf.readShortLE();
    }
    
    public int decodeUShort() {

    	if(this.getServer().getEndian() == Endian.BIG) {

    		return byteBuf.readUnsignedShort();
    	}
    	
    	return byteBuf.readUnsignedShortLE();
    }
    
    
    public int decodeInt() {

    	if(this.getServer().getEndian() == Endian.BIG) {

    		return byteBuf.readInt();
    	}
    	
    	return byteBuf.readIntLE();
    }
    
    public long decodeUInt() {

    	if(this.getServer().getEndian() == Endian.BIG) {

    		return byteBuf.readUnsignedInt();
    	}

    	return byteBuf.readUnsignedIntLE();
    }
    
    public long decodeLong() {

    	if(this.getServer().getEndian() == Endian.BIG) {

    		return byteBuf.readLong();
    	}
    	
    	return byteBuf.readLongLE();
    }
    
    public float decodeFloat() {

    	if(this.getServer().getEndian() == Endian.BIG) {

    		return byteBuf.readFloat();
    	}
    	
    	return byteBuf.readFloatLE();
    }
    
    public double decodeDouble() {

    	if(this.getServer().getEndian() == Endian.BIG) {

    		return byteBuf.readDouble();
    	}
    	
    	return byteBuf.readDoubleLE();
    }
    
    public String decodeString() {

    	long size = this.decodeUInt();

		byte[] bs = this.decode((int)size);

    	return new String(bs, this.getServer().getCharset());
    }
    
	public Map<?,?> decodeMap() throws JsonProcessingException {

    	return ProjectBeanHolder.getObjectMapper().readValue(decodeString(), Map.class);
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

		return new InboundPacket(byteBuf, this.getServer());
	}

	@Override
	public void dispose() {

		byteBuf.release();
	}
}
