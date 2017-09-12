/**
  * MIDIデータのTrackChunkを表す
  * @author Yutaro Inoue
  */
import java.util.*;


class TrackChunk {
    byte[] track;
    int trackDataSize;
    ArrayList<HashMap> trackData = new ArrayList<HashMap>();

    private byte[] tmpData;

    TrackChunk(byte[] track, int trackDataSize) {
        this.track = track;
        this.trackDataSize = trackDataSize;
        tmpData = Arrays.copyOfRange(track, 8, track.length);
        //ここからデルタタイムとイベントの解析
        while(true) {
            //printByte(tmpData);
            byte[] deltaTime = getDeltaTime(tmpData);
            byte[] event = getEvent(tmpData);
            System.out.print("delta time : ");
            printByte(deltaTime);
            System.out.print("event : ");
            printByte(event);
            HashMap<String, byte[]> map = new HashMap<String, byte[]>();
            map.put("deltaTime", deltaTime);
            map.put("event", event);
            trackData.add(map);
            if(event.length == 3) {
                if(event[0] == (byte)0xff && event[1] == (byte)0x2f && event[2] == (byte)0x00) {
                    break;
                }
            }
        }
    }

    public byte[] createWave() {
      
    }

    public void printByte(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%02X", b) + " ");
        }
        String str = sb.toString();
        System.out.println(str);
    }

    private byte[] getDeltaTime(byte[] trackData) {
        ArrayList<Byte> time = new ArrayList<Byte>();
        int i = 0;
        //最上位ビットが1の時ループ
        while ((trackData[i] & (byte)0x80) == (byte)0x80) {
            time.add(trackData[i]);
            i++;
        }
        //デルタタイムが0x7f以下の時値を保持する
        time.add(trackData[i]);
        //globalのtrackData(tmpData)からデルタタイムを除去する
        tmpData = Arrays.copyOfRange(trackData, time.size(), trackData.length);
        //返却する配列を用意する
        byte[] deltaTime = new byte[time.size()];
        for(int j = 0; j < time.size(); j++) {
            deltaTime[j] = time.get(j);
        }
        return deltaTime;
    }

    private byte[] getEvent(byte[] trackData) {
        byte[] event = null;
        //イベント種別を判別
        if (trackData[0] >= (byte)0x80 && trackData[0] <= (byte)0xef) {
            event = getMidiEvent(trackData);
        } else if (trackData[0] == (byte)0xf0 || trackData[0] == (byte)0xf7) {
            event = getSysExEvent(trackData);
        } else if (trackData[0] == (byte)0xff) {
            event = getMetaEvent(trackData);
        }
        return event;
    }

    private byte[] getMidiEvent(byte[] trackData) {
        System.out.println("Midi event");
        int size = 3;
        if(trackData[0] >= (byte)0xc0 && trackData[0] <= (byte)0xdf) {
          size = 2;
        }
        byte[] midiEvent = Arrays.copyOfRange(trackData, 0, size);
        //globalのtrackData(tmpData)からmidiイベントを除去する
        tmpData = Arrays.copyOfRange(trackData, size, tmpData.length);
        return midiEvent;

    }

    private byte[] getSysExEvent(byte[] trackData) {
        System.out.println("SysEx event");
        int size = Byte.toUnsignedInt(trackData[1]) + 2;
        byte[] sysExEvent = Arrays.copyOfRange(trackData, 0, size);
        //globalのtrackData(tmpData)からSysExイベントを除去する
        tmpData = Arrays.copyOfRange(trackData, size, tmpData.length);
        return sysExEvent;
    }

    private byte[] getMetaEvent(byte[] trackData) {
        System.out.println("Meta event");
        int size = Byte.toUnsignedInt(trackData[2]) + 3;
        byte[] metaEvent = Arrays.copyOfRange(trackData, 0, size);
        //globalのtrackData(tmpData)からMetaイベントを除去する
        tmpData = Arrays.copyOfRange(trackData, size, tmpData.length);
        return metaEvent;
    }


}
