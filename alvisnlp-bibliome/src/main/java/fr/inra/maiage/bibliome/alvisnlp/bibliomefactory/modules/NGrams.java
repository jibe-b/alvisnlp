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

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.SectionModule.SectionResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
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

@AlvisNLPModule
public abstract class NGrams extends SectionModule<SectionResolvedObjects> implements AnnotationCreator {
	private String tokenLayerName = DefaultNames.getWordLayer();
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private String targetLayerName;
	private Integer maxNGramSize;
	private String[] keepAnnotations = new String[] {};
	
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
			Layer target = sec.ensureLayer(targetLayerName);
			for (Layer sentence : sec.getSentences(tokenLayerName, sentenceLayerName)) {
				int len = sentence.size();
				for (int start = 0; start < len; ++start) {
					int maxEnd = start + maxNGramSize - 1;
					if (maxEnd >= len)
						maxEnd = len - 1;
					for (int end = maxEnd; end >= start; --end) {
						n++;
						Annotation ngram = getNGram(sec, sentence.get(start).getStart(), sentence.get(end).getEnd());
						target.add(ngram);
					}
				}
			}
		}
		getLogger(ctx).info("ngrams: " + n);
	}
	
	private Annotation getNGram(Section sec, int startPosition, int endPosition) {
		for (String ln : keepAnnotations) {
			if (!sec.hasLayer(ln))
				continue;
			Layer span = sec.getLayer(ln).span(startPosition, endPosition);
			if (span.isEmpty())
				continue;
			return span.get(0);
		}
		return new Annotation(this, sec, startPosition, endPosition);
	}

	@Override
	protected String[] addLayersToSectionFilter() {
		return new String[] { tokenLayerName };
	}
	
	@Override
	protected String[] addFeaturesToSectionFilter() {
		return null;
	}

	@Param(nameType=NameType.LAYER)
	public String getTokenLayerName() {
		return tokenLayerName;
	}

	@Param(nameType=NameType.LAYER)
	public String getTargetLayerName() {
		return targetLayerName;
	}

	@Param
	public Integer getMaxNGramSize() {
		return maxNGramSize;
	}

	@Param(nameType=NameType.LAYER)
	public String[] getKeepAnnotations() {
		return keepAnnotations;
	}

	@Param(nameType=NameType.LAYER, mandatory=false)
	public String getSentenceLayerName() {
		return sentenceLayerName;
	}

	public void setSentenceLayerName(String sentenceLayerName) {
		this.sentenceLayerName = sentenceLayerName;
	}

	public void setTokenLayerName(String tokenLayerName) {
		this.tokenLayerName = tokenLayerName;
	}

	public void setTargetLayerName(String targetLayerName) {
		this.targetLayerName = targetLayerName;
	}

	public void setMaxNGramSize(Integer maxNGramSize) {
		this.maxNGramSize = maxNGramSize;
	}

	public void setKeepAnnotations(String[] keepAnnotations) {
		this.keepAnnotations = keepAnnotations;
	}
}
