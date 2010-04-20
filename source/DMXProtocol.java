import javax.sound.midi.*;

class DMXProtocol implements ConnectionListener, BatchListener
{
	private Configuration m_Configuration;
	
	DMXProtocol(Configuration Configuration)
	{
		m_Configuration = Configuration;
		m_Configuration.addConnectionListener(this);
	}
	
	public void enterBatch()
	{
	}
	
	public void leaveBatch()
	{
		dumpMatrix();
	}
	
	public void connectionChanged(ConnectionEvent Event)
	{
		if(Event.isPartOfBatch() == false)
		{
			dumpMatrix();
		}
	}
	
	/**
	 * @format
	 * <F0> <3F> <Channel> <MessageType = 00> <Size> <00>
	 * { Size
	 *     <Destination> <Source> <00>
	 * }
	 * <00> <F7>
	 **/
	public void dumpMatrix()
	{
		if(m_Configuration.isMIDIDeviceOpen() == true)
		{
			SysexMessage Command = null;
			
			try
			{
				Command = new SysexMessage();
				
				byte [] Message = new byte[8 + 3 * m_Configuration.getSize()];
				int iIndex = 0;
				
				Message[iIndex++] = (byte)0xF0;
				Message[iIndex++] = (byte)0x3F;
				Message[iIndex++] = (byte)m_Configuration.getIDNumber();
				Message[iIndex++] = (byte)0x00;
				Message[iIndex++] = (byte)m_Configuration.getSize();
				Message[iIndex++] = (byte)0x00;
				for(int iDestination = 0; iDestination < m_Configuration.getSize(); ++iDestination)
				{
					Message[iIndex++] = (byte)iDestination;
					Message[iIndex++] = (byte)(m_Configuration.getConnectedSource(iDestination) + 1);
					Message[iIndex++] = (byte)0x00;
				}
				Message[iIndex++] = (byte)0x00;
				Message[iIndex++] = (byte)0xF7;
				Command.setMessage(Message, Message.length);
				System.out.println("Accumulated " + Message.length + " = " + iIndex + " bites.");
			}
			catch(InvalidMidiDataException invalidmididataexception)
			{
				System.out.println("Invalid Midi Data Exception!");
			}
			System.out.println("Sending Dump (Protocol A).");
			m_Configuration.sendMIDI(Command);
		}
	}
	
	/**
	 * @format
	 * <F0> <3F> <Channel> <MessageType = 01> <DestinationSize>
	 * { DestinationSize
	 *     <DestinationName = 16 * Character or padded with ' '>
	 * }
	 * <SourceSize>
	 * { SourceSize
	 *     <SourceName = 16 * Character or padded with ' '>
	 * }
	 * <PresetSize>
	 * { PresetSize
	 *     <PresetNumber> <PresetName = 16 * Character or padded with ' '> <MatrixSize>
	 *     { MatrixSize
	 *         <Destination> <Source> <00>
	 *     }
	 *     <00>
	 * }
	 * <F7>
	 **/
	public void dumpPresets()
	{
		if(m_Configuration.isMIDIDeviceOpen() == true)
		{
			SysexMessage Command = null;
			
			try
			{
				Command = new SysexMessage();
				
				System.out.println("Size: " + m_Configuration.getSize() + " x " + m_Configuration.getSize());
				System.out.println("Presets: " + StaticConfiguration.getNumberOfPresets());
				
				int ArrayLength = 5 + 1 + 2 * 16 * m_Configuration.getSize() + 1 + StaticConfiguration.getNumberOfPresets() * (1 + 16 + 1 + 3 * m_Configuration.getSize() + 1) + 1;
				
				System.out.println("ArrayLength: " + ArrayLength);
				
				byte [] Message = new byte[ArrayLength];
				
				System.out.println("Allocated: " + Message.length);
				
				int iIndex = 0;
				
				Message[iIndex++] = (byte)0xF0;
				Message[iIndex++] = (byte)0x3F;
				Message[iIndex++] = (byte)m_Configuration.getIDNumber();
				Message[iIndex++] = (byte)0x01;
				Message[iIndex++] = (byte)m_Configuration.getSize();
				for(int iDestination = 0; iDestination < m_Configuration.getSize(); ++iDestination)
				{
					for(int iCharacter = 0; iCharacter < 16; ++iCharacter)
					{
						if(iCharacter < m_Configuration.getDestinationName(iDestination).length())
						{
							Message[iIndex++] = (byte)m_Configuration.getDestinationName(iDestination).charAt(iCharacter);
						}
						else
						{
							Message[iIndex++] = (byte)0x20;
						}
					}
				}
				Message[iIndex++] = (byte)m_Configuration.getSize();
				for(int iSource = 0; iSource < m_Configuration.getSize(); ++iSource)
				{
					for(int iCharacter = 0; iCharacter < 16; ++iCharacter)
					{
						if(iCharacter < m_Configuration.getSourceName(iSource).length())
						{
							Message[iIndex++] = (byte)m_Configuration.getSourceName(iSource).charAt(iCharacter);
						}
						else
						{
							Message[iIndex++] = (byte)0x20;
						}
					}
				}
				Message[iIndex++] = StaticConfiguration.getNumberOfPresets().byteValue();
				for(int iPreset = 0; iPreset < StaticConfiguration.getNumberOfPresets(); ++iPreset)
				{
					Message[iIndex++] = (byte)iPreset;
					for(int iCharacter = 0; iCharacter < 16; ++iCharacter)
					{
						if(iCharacter < m_Configuration.getPreset(iPreset).getName().length())
						{
							Message[iIndex++] = (byte)m_Configuration.getPreset(iPreset).getName().charAt(iCharacter);
						}
						else
						{
							Message[iIndex++] = (byte)0x20;
						}
					}
					Message[iIndex++] = (byte)m_Configuration.getSize();
					
					int [] Matrix = m_Configuration.getPreset(iPreset).getMatrix();
					
					for(int iDestination = 0; iDestination < m_Configuration.getSize(); ++iDestination)
					{
						Message[iIndex++] = (byte)iDestination;
						if(Matrix != null)
						{
							Message[iIndex++] = (byte)(Matrix[iDestination] + 1);
						}
						else
						{
							Message[iIndex++] = (byte)0x00;;
						}
						Message[iIndex++] = (byte)0x00;
					}
					Message[iIndex++] = (byte)0x00;
				}
				Message[iIndex++] = (byte)0xF7;
				Command.setMessage(Message, Message.length);
				System.out.println("Accumulated " + Message.length + " == " + iIndex + " bites.");
			}
			catch(InvalidMidiDataException invalidmididataexception)
			{
				System.out.println("Invalid Midi Data Exception!");
			}
			System.out.println("Sending Dump (Protocol B).");
			m_Configuration.sendMIDI(Command);
			m_Configuration.reopenMIDIDevice();
		}
	}
	
	/**
	 * @format
	 * <F0> <3F> <Channel> <MessageType = 01> <DestinationSize>
	 * { DestinationSize
	 *     <DestinationName = 16 * Character or padded with ' '>
	 * }
	 * <SourceSize>
	 * { SourceSize
	 *     <SourceName = 16 * Character or padded with ' '>
	 * }
	 * <PresetSize>
	 * { PresetSize
	 *     <PresetNumber> <PresetName = 16 * Character or padded with ' '> <MatrixSize>
	 *     { MatrixSize
	 *         <Source>
	 *     }
	 *     <00>
	 * }
	 * <F7>
	 **/
	public void dumpC()
	{
		if(m_Configuration.isMIDIDeviceOpen() == true)
		{
			SysexMessage Command = null;
			
			try
			{
				Command = new SysexMessage();
				
				System.out.println("Size: " + m_Configuration.getSize() + " x " + m_Configuration.getSize());
				System.out.println("Presets: " + StaticConfiguration.getNumberOfPresets());
				
				int ArrayLength = 5 + 1 + 2 * 16 * m_Configuration.getSize() + 1 + StaticConfiguration.getNumberOfPresets() * (1 + 16 + 1 + m_Configuration.getSize() + 1) + 1;
				
				System.out.println("ArrayLength: " + ArrayLength);
				
				byte [] Message = new byte[ArrayLength];
				
				System.out.println("Allocated: " + Message.length);
				
				int iIndex = 0;
				
				Message[iIndex++] = (byte)0xF0;
				Message[iIndex++] = (byte)0x3F;
				Message[iIndex++] = (byte)m_Configuration.getIDNumber();
				Message[iIndex++] = (byte)0x01;
				Message[iIndex++] = (byte)m_Configuration.getSize();
				for(int iDestination = 0; iDestination < m_Configuration.getSize(); ++iDestination)
				{
					for(int iCharacter = 0; iCharacter < 16; ++iCharacter)
					{
						if(iCharacter < m_Configuration.getDestinationName(iDestination).length())
						{
							Message[iIndex++] = (byte)m_Configuration.getDestinationName(iDestination).charAt(iCharacter);
						}
						else
						{
							Message[iIndex++] = (byte)0x20;
						}
					}
				}
				Message[iIndex++] = (byte)m_Configuration.getSize();
				for(int iSource = 0; iSource < m_Configuration.getSize(); ++iSource)
				{
					for(int iCharacter = 0; iCharacter < 16; ++iCharacter)
					{
						if(iCharacter < m_Configuration.getSourceName(iSource).length())
						{
							Message[iIndex++] = (byte)m_Configuration.getSourceName(iSource).charAt(iCharacter);
						}
						else
						{
							Message[iIndex++] = (byte)0x20;
						}
					}
				}
				Message[iIndex++] = StaticConfiguration.getNumberOfPresets().byteValue();
				for(int iPreset = 0; iPreset < StaticConfiguration.getNumberOfPresets(); ++iPreset)
				{
					Message[iIndex++] = (byte)iPreset;
					for(int iCharacter = 0; iCharacter < 16; ++iCharacter)
					{
						if(iCharacter < m_Configuration.getPreset(iPreset).getName().length())
						{
							Message[iIndex++] = (byte)m_Configuration.getPreset(iPreset).getName().charAt(iCharacter);
						}
						else
						{
							Message[iIndex++] = (byte)0x20;
						}
					}
					Message[iIndex++] = (byte)m_Configuration.getSize();
					
					int [] Matrix = m_Configuration.getPreset(iPreset).getMatrix();
					
					for(int iDestination = 0; iDestination < m_Configuration.getSize(); ++iDestination)
					{
						if(Matrix != null)
						{
							Message[iIndex++] = (byte)(Matrix[iDestination] + 1);
						}
						else
						{
							Message[iIndex++] = (byte)0x00;;
						}
					}
					Message[iIndex++] = (byte)0x00;
				}
				Message[iIndex++] = (byte)0xF7;
				Command.setMessage(Message, Message.length);
				System.out.println("Accumulated " + Message.length + " == " + iIndex + " bites.");
			}
			catch(InvalidMidiDataException invalidmididataexception)
			{
				System.out.println("Invalid Midi Data Exception!");
			}
			System.out.println("Sending Dump (Protocol C).");
			m_Configuration.sendMIDI(Command);
			m_Configuration.reopenMIDIDevice();
		}
	}
}
