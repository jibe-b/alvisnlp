/*
Copyright 2016, 2017 Institut National de la Recherche Agronomique

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/



package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.EvaluationContext;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;

// TODO: Auto-generated Javadoc
/**
 * Matches a regular expression on section contents.
 */
@AlvisNLPModule
public abstract class RegExp extends SectionModule<SectionResolvedObjects> implements AnnotationCreator {
    /**
     * Gets the pattern.
     * 
     * @return the pattern
     */
    @Param(defaultDoc = "Regular expression to match.")
    public abstract Pattern getPattern();

    /**
     * Sets the pattern.
     * 
     * @param pattern
     *            the new pattern
     */
    public abstract void setPattern(Pattern pattern);

    /**
     * Gets the target layer name.
     * 
     * @return the target layer name
     */
    @Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer where to store matches.")
    public abstract String getTargetLayerName();
    
    /**
     * Sets the target layer name.
     * 
     * @param targetLayerName
     *            the new target layer name
     */
    public abstract void setTargetLayerName(String targetLayerName);
    
    @Override
    public String[] addFeaturesToSectionFilter() {
        return new String[] {};
    }

    @Override
    public String[] addLayersToSectionFilter() {
        return new String[] {};
    }

    @Override
	protected SectionResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new SectionResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		Logger logger = getLogger(ctx);
		EvaluationContext evalCtx = new EvaluationContext(logger);
    	int n = 0;
        for (Section sec : Iterators.loop(sectionIterator(evalCtx, corpus))) {
            Matcher matcher = getPattern().matcher(sec.getContents());
            Layer layer = sec.ensureLayer(getTargetLayerName());
            while (matcher.find()) {
            	n++;
                new Annotation(this, layer, matcher.start(), matcher.end());
            }
        }
        if (n == 0) {
        	logger.warning("found no matches");
        }
        else {
        	logger.info("found " + n + " matches");
        }
    }
}
