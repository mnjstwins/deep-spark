/*
 * Copyright 2014, Stratio.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stratio.deep.es.extractor;

import java.lang.reflect.InvocationTargetException;

import org.elasticsearch.hadoop.mr.EsInputFormat;
import org.elasticsearch.hadoop.mr.EsOutputFormat;
import org.elasticsearch.hadoop.mr.LinkedMapWritable;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.stratio.deep.commons.config.DeepJobConfig;
import com.stratio.deep.commons.entity.Cells;
import com.stratio.deep.commons.exception.DeepTransformException;
import com.stratio.deep.commons.extractor.impl.GenericHadoopExtractor;
import com.stratio.deep.es.config.ESDeepJobConfig;
import com.stratio.deep.es.utils.UtilES;

import scala.Tuple2;

/**
 * CellRDD to interact with ES
 */
public final class ESCellExtractor
        extends GenericHadoopExtractor<Cells, ESDeepJobConfig<Cells>, Object, LinkedMapWritable, Object, JSONObject> {

    private static final Logger LOG = LoggerFactory.getLogger(ESCellExtractor.class);
    private static final long serialVersionUID = -3208994171892747470L;

    public ESCellExtractor() {
        this(Cells.class);
    }

    public ESCellExtractor(Class<Cells> cellsClass) {
        super();
        this.deepJobConfig = new ESDeepJobConfig(cellsClass);
        this.inputFormat = new EsInputFormat<>();
        this.outputFormat = new EsOutputFormat();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Cells transformElement(Tuple2<Object, LinkedMapWritable> tuple,
                                  DeepJobConfig<Cells, ? extends DeepJobConfig> config) {

        try {
            return UtilES.getCellFromJson(tuple._2(), deepJobConfig.getNameSpace());
        } catch (Exception e) {
            LOG.error("Cannot convert JSON: ", e);
            throw new DeepTransformException("Could not transform from Json to Cell " + e.getMessage());
        }
    }

    @Override
    public Tuple2<Object, JSONObject> transformElement(Cells record) {
        try {
            return new Tuple2<>(null, UtilES.getJsonFromCell(record));
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            LOG.error(e.getMessage());
            throw new DeepTransformException(e.getMessage());
        }

    }

}
