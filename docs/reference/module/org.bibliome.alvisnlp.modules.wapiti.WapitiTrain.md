# org.bibliome.alvisnlp.modules.wapiti.WapitiTrain

## Synopsis

synopsis

**This module is experimental.**

## Description

synopsis

## Parameters

<a name="features">

### features

Optional

Type: [Expression[]](../converter/alvisnlp.corpus.expressions.Expression[])



<a name="modelFile">

### modelFile

Optional

Type: [OutputFile](../converter/org.bibliome.util.files.OutputFile)



<a name="wapitiExecutable">

### wapitiExecutable

Optional

Type: [ExecutableFile](../converter/org.bibliome.util.files.ExecutableFile)



<a name="commandLineOptions">

### commandLineOptions

Optional

Type: [String[]](../converter/java.lang.String[])



<a name="modelType">

### modelType

Optional

Type: [String](../converter/java.lang.String)



<a name="patternFile">

### patternFile

Optional

Type: [InputFile](../converter/org.bibliome.util.files.InputFile)



<a name="trainAlgorithm">

### trainAlgorithm

Optional

Type: [String](../converter/java.lang.String)



<a name="documentFilter">

### documentFilter

Default value: `true`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Only process document that satisfy this filter.

<a name="sectionFilter">

### sectionFilter

Default value: `boolean:and(true, nav:layer:words())`

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Process only sections that satisfy this filter.

<a name="sentenceLayerName">

### sentenceLayerName

Default value: `sentences`

Type: [String](../converter/java.lang.String)



<a name="tokenLayerName">

### tokenLayerName

Default value: `words`

Type: [String](../converter/java.lang.String)


