
import java.io.IOException;
import java.io.OutputStream;

public class TeeOutputStream
        extends OutputStream
{

    OutputStream tee = null, out = null;

    public TeeOutputStream(OutputStream chainedStream,
                           OutputStream teeStream)
    {
        out = chainedStream;

        if (teeStream == null)
        {
            tee = System.out;
        }
        else
        {
            tee = teeStream;
        }
    }

    /**
     * Implementation for parent's abstract write method. This writes out the
     * passed in character to the both, the chained stream and "tee" stream.
     */
    @Override
    public void write(int c) throws IOException
    {
        out.write(c);

        tee.write(c);
        tee.flush();
    }

    /**
     * Closes both, chained and tee, streams.
     */
    @Override
    public void close() throws IOException
    {
        flush();

        out.close();
        tee.close();
    }

    /**
     * Flushes chained stream; the tee stream is flushed each time a character
     * is written to it.
     */
    @Override
    public void flush() throws IOException
    {
        out.flush();

    }
}