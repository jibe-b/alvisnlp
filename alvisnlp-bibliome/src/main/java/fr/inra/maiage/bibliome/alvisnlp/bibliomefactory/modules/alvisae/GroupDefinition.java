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


package fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisae;

import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisae.AlvisAEWriter.CadixeExportContext;
import fr.inra.maiage.bibliome.alvisnlp.bibliomefactory.modules.alvisae.GroupDefinition.Resolved;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import fr.inra.maiage.bibliome.alvisnlp.core.corpus.Element;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Evaluator;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Expression;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.LibraryResolver;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.Resolvable;
import fr.inra.maiage.bibliome.alvisnlp.core.corpus.expressions.ResolverException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.ModuleException;
import fr.inra.maiage.bibliome.alvisnlp.core.module.NameUsage;
import fr.inra.maiage.bibliome.alvisnlp.core.module.types.ExpressionMapping;
import fr.inra.maiage.bibliome.util.Iterators;

public class GroupDefinition extends AnnotationDefinition implements Resolvable<Resolved> {
	private final Expression items;

	GroupDefinition(Expression elements, ExpressionMapping propsMap, Expression properties, Expression propKey, Expression propValue, Expression sources, Expression sourceId, Expression sourceAnnotationSet, Expression type, Expression items) {
		super(elements, propsMap, properties, propKey, propValue, sources, sourceId, sourceAnnotationSet, type, 1);
		this.items = items;
	}

	public static class Resolved extends AnnotationDefinition.Resolved {
		private final Evaluator items;

		public Resolved(LibraryResolver resolver, GroupDefinition aDef) throws ResolverException {
			super(resolver, aDef);
			this.items = aDef.items.resolveExpressions(resolver);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		protected void fillObject(JSONObject object, Element elt, int offset, CadixeExportContext ctx) {
			JSONArray group = new JSONArray();
			for (Element item : Iterators.loop(items.evaluateElements(ctx.evalCtx, elt))) {
				AnnotationReference aRef = ctx.getAnnotationReference(item);
				group.add(aRef.asJSON());
			}
			object.put("group", group);
		}

		@Override
		public void collectUsedNames(NameUsage nameUsage, String defaultType) throws ModuleException {
			super.collectUsedNames(nameUsage, defaultType);
			if (items != null) {
				items.collectUsedNames(nameUsage, defaultType);
			}
		}		
	}

	@Override
	public GroupDefinition.Resolved resolveExpressions(LibraryResolver resolver) throws ResolverException {
		return new GroupDefinition.Resolved(resolver, this);
	}
}
