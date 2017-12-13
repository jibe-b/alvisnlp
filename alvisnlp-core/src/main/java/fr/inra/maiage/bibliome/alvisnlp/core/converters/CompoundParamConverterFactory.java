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


package fr.inra.maiage.bibliome.alvisnlp.core.converters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import fr.inra.maiage.bibliome.util.service.CompositeServiceFactory;

/**
 * Compound converter factory.
 * @author rbossy
 *
 */
public class CompoundParamConverterFactory extends CompositeServiceFactory<Class<?>,ParamConverter,ParamConverterFactory> implements ParamConverterFactory {
    @Override
    public Collection<String> autoAlias(Class<?> key) {
        return Arrays.asList(key.getCanonicalName(), key.getSimpleName());
    }

	@Override
	public List<String> getResourceBases() {
		List<String> result = new ArrayList<String>();
		for (ParamConverterFactory f : getFactories()) {
			result.addAll(f.getResourceBases());
		}
		return result;
	}
}
