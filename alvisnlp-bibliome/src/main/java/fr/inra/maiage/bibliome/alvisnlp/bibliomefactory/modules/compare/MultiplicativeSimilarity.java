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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.compare;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;

public class MultiplicativeSimilarity implements ElementSimilarity {
	private final ElementSimilarity[] similarities;

	public MultiplicativeSimilarity(ElementSimilarity... similarities) {
		super();
		this.similarities = similarities;
	}

	@Override
	public double similarity(Element a, Element b) {
		double result = 1;
		for (ElementSimilarity sim : similarities)
			result *= sim.similarity(a, b);
		return result;
	}

	@Override
	public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
		for (ElementSimilarity esim : similarities) {
			esim.collectUsedNames(nameUsage, defaultType);
		}
	}
}
