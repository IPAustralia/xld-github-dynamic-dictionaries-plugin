/**
 * Copyright 2021 XEBIALABS
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/**
 <<<<<<< HEAD
 * Copyright 2017 XebiaLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 =======
 * Copyright 2021 XEBIALABS
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
/**
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 * FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
 >>>>>>> f180145... upgrade using the new Public API
 */
package ext.deployit.community.ci.dictionary;


import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.xebialabs.deployit.plugin.api.udm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.cache.*;

@Metadata(
    root = Metadata.ConfigurationItemRoot.ENVIRONMENTS,
    description = "A Dictionary that resolves the value dynamically",
    virtual = true
)
@TypeIcon(value = "icons/types/dictionary-logo.svg")
public abstract class BaseDynamicDictionary extends AbstractDictionary implements IEncryptedDictionary {

    @Property(description = "Use a cache (5 seconds)...", defaultValue = "True", category = "Others")
    private boolean useCache;

    @Override
    public Map<String, String> getEntries() {

        if (logger.isDebugEnabled()) {
            logger.debug(this + " getEntries");
        }

        Map data;
        if (useCache) {
            data = cache.getUnchecked(this);
        } else {
            data = loadData();
        }
        return data;
    }

    @Override
    public String getValue(String s) {
        if (logger.isDebugEnabled()) {
            logger.debug(this + " getValue [" + s + "]");
        }
        return this.getEntries().get(s);
    }

    private static LoadingCache<BaseDynamicDictionary, Map<String, String>> cache =
        CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.SECONDS)
            .removalListener(new RemovalListener<Object, Object>() {
                @Override
                public void onRemoval(final RemovalNotification<Object, Object> removalNotification) {
                    logger.info("Remove " + removalNotification.getKey() + " from dictionary cache.....");
                }
            })
            .build(new CacheLoader<BaseDynamicDictionary, Map<String, String>>() {
                @Override
                public Map<String, String> load(final BaseDynamicDictionary s) throws Exception {
                    return s.loadData();
                }
            });

    public Map<String, String> loadData() {
        logger.trace("Return empty data");
        return Collections.emptyMap();
    }


    @Override
    public Map<String, String> getEncryptedEntries() {
        logger.trace("Return empty encrypted entries");
        return Collections.emptyMap();
    }

    private static Logger logger = LoggerFactory.getLogger(BaseDynamicDictionary.class);
}
