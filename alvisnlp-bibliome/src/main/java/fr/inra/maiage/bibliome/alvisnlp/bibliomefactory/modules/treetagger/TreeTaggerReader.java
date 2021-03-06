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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.treetagger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.CorpusModule;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.ResolvedObjects;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Annotation;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Corpus;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.DefaultNames;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Document;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Layer;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.NameType;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Section;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.AnnotationCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.DocumentCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.creators.SectionCreator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingContext;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ProcessingException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.AlvisNLPModule;
import fr.inra.maiage.bibliome.alvisnlp.core.module.lib.Param;
import fr.inra.maiage.bibliome.util.Iterators;
import fr.inra.maiage.bibliome.util.StringCat;
import fr.inra.maiage.bibliome.util.filelines.InvalidFileLineEntry;
import fr.inra.maiage.bibliome.util.filelines.RecordFileLines;
import fr.inra.maiage.bibliome.util.streams.SourceStream;

@AlvisNLPModule
public abstract class TreeTaggerReader extends CorpusModule<ResolvedObjects> implements DocumentCreator, SectionCreator, AnnotationCreator {
	private String sectionName = null;
	private String wordLayerName = DefaultNames.getWordLayer();
	private String sentenceLayerName = DefaultNames.getSentenceLayer();
	private String posFeatureKey = null;
	private String lemmaFeatureKey = null;
	private String charset = "UTF-8";
	private SourceStream sourcePath;
	
	private static final RecordFileLines recordFileLines = new RecordFileLines();

	private static final String getSectionContents(List<List<String>> tokens) {
		StringCat sb = new StringCat();
		for (List<String> t : tokens) {
			if (!sb.isEmpty())
				sb.append(" ");
			sb.append(t.get(0));
		}
		return sb.toString();
	}
	
	private void fillLayers(Layer wordLayer, Layer sentenceLayer, List<List<String>> tokens) throws ProcessingException {
		int wordStart = 0;
		int sentenceStart = 0;
		for (int i = 0; i < tokens.size(); ++i) {
			List<String> t = tokens.get(i);
			if (t.size() != 3)
				throw new ProcessingException("malformed line in: " + wordLayer.getSection().getDocument().getId());
			String surface = t.get(0);
			int end = wordStart + surface.length();
			String pos = t.get(1);
			if ("SENT".equals(pos)) {
				new Annotation(this, sentenceLayer, sentenceStart, end);
				sentenceStart = end + 1;
			}
			Annotation a = new Annotation(this, wordLayer, wordStart, end);
			if (posFeatureKey != null)
				a.addFeature(posFeatureKey, pos);
			if (lemmaFeatureKey != null)
				a.addFeature(lemmaFeatureKey, t.get(2));
			wordStart = end + 1;
			tokens.set(i, null);
		}	
	}

	@Override
	protected ResolvedObjects createResolvedObjects(ProcessingContext<Corpus> ctx) throws ResolverException {
		return new ResolvedObjects(ctx, this);
	}

	@Override
	public void process(ProcessingContext<Corpus> ctx, Corpus corpus) throws ModuleException {
		try {
			for (BufferedReader r : Iterators.loop(sourcePath.getBufferedReaders())) {
				processFile(corpus, r);
				r.close();
			}
		}
		catch (IOException e) {
			rethrow(e);
		}
	}

	private void processFile(Corpus corpus, BufferedReader reader) throws ModuleException, IOException {
		try {
			String name = sourcePath.getStreamName(reader);
			
			List<List<String>> tokens = new ArrayList<List<String>>();
			recordFileLines.process(reader, tokens);
			reader.close();

			Document doc = Document.getDocument(this, corpus, name);
			Section sec = new Section(this, doc, sectionName, getSectionContents(tokens));
			fillLayers(sec.ensureLayer(wordLayerName), sec.ensureLayer(sentenceLayerName), tokens);
		} catch (InvalidFileLineEntry ifle) {
			rethrow(ifle);
		}
	}

	@Param(defaultDoc = "Name of the section of each document.")
	public String getSectionName() {
		return sectionName;
	}

	@Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer where to store word annotations.")
	public String getWordLayerName() {
		return wordLayerName;
	}

	@Param(nameType=NameType.LAYER, defaultDoc = "Name of the layer where to store sentence annotations.")
	public String getSentenceLayerName() {
		return sentenceLayerName;
	}

	@Param(mandatory=false, defaultDoc = "Name of the feature where to store word POS tags.")
	public String getPosFeatureKey() {
		return posFeatureKey;
	}

	@Param(mandatory=false, defaultDoc = "Name of the feature where to store word lemmas.")
	public String getLemmaFeatureKey() {
		return lemmaFeatureKey;
	}
	
	@Param(defaultDoc = "Character set of input files.")
	public String getCharset() {
		return charset;
	}

	@Param
	public SourceStream getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(SourceStream sourcePath) {
		this.sourcePath = sourcePath;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setPosFeatureKey(String posFeatureKey) {
		this.posFeatureKey = posFeatureKey;
	}

	public void setLemmaFeatureKey(String lemmaFeatureKey) {
		this.lemmaFeatureKey = lemmaFeatureKey;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public void setWordLayerName(String wordLayerName) {
		this.wordLayerName = wordLayerName;
	}

	public void setSentenceLayerName(String sentenceLayerName) {
		this.sentenceLayerName = sentenceLayerName;
	}
}
