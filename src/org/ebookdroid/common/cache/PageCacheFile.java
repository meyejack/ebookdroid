package org.ebookdroid.common.cache;

import org.ebookdroid.core.codec.CodecPageInfo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.emdev.common.log.LogContext;

public class PageCacheFile extends File {

    private static final long serialVersionUID = 6836895806027391288L;

    private static final LogContext LCTX = CacheManager.LCTX;

    PageCacheFile(final File dir, final String name) {
        super(dir, name);
    }

    public CodecPageInfo[] load() {
        try {
            LCTX.d("Loading pages cache...");
            final DataInputStream in = new DataInputStream(new FileInputStream(this));
            try {
                final int pages = in.readInt();
                final CodecPageInfo[] infos = new CodecPageInfo[pages];

                for (int i = 0; i < infos.length; i++) {
                    infos[i] = new CodecPageInfo(in.readInt(), in.readInt());
                    if (infos[i].width == -1 || infos[i].height == -1) {
                        return null;
                    }
                }
                LCTX.d("Loading pages cache finished");
                return infos;
            } catch (final EOFException ex) {
                LCTX.e("Loading pages cache failed: " + ex.getMessage());
            } catch (final IOException ex) {
                LCTX.e("Loading pages cache failed: " + ex.getMessage());
            } finally {
                try {
                    in.close();
                } catch (final IOException ex) {
                }
            }
        } catch (final FileNotFoundException ex) {
            LCTX.e("Loading pages cache failed: " + ex.getMessage());
        }
        return null;
    }

    public void save(final CodecPageInfo[] infos) {
        try {
            LCTX.d("Saving pages cache...");
            final DataOutputStream out = new DataOutputStream(new FileOutputStream(this));
            try {
                out.writeInt(infos.length);
                for (int i = 0; i < infos.length; i++) {
                    if (infos[i] != null) {
                        out.writeInt(infos[i].width);
                        out.writeInt(infos[i].height);
                    } else {
                        out.writeInt(-1);
                        out.writeInt(-1);
                    }
                }
                LCTX.d("Saving pages cache finished");
            } catch (final IOException ex) {
                LCTX.e("Saving pages cache failed: " + ex.getMessage());
            } finally {
                try {
                    out.close();
                } catch (final IOException ex) {
                }
            }
        } catch (final IOException ex) {
            LCTX.e("Saving pages cache failed: " + ex.getMessage());
        }
    }
}
