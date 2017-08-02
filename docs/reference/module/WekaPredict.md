# org.bibliome.alvisnlp.modules.weka.WekaPredict

## Synopsis

Classifies elements with a Weka classifier.

## Description

*org.bibliome.alvisnlp.modules.weka.WekaPredict* applies the classifier loaded from [classifier](#classifier) on elements specified by [examples](#examples).

## Parameters

<a name="classifierFile">

### classifierFile

Optional

Type: [File](../converter/java.io.File)

Serialized classifier file. This file must be generated by [TrainingElementClassifier](../module/TrainingElementClassifier) with the same [relationDefinition](#relationDefinition).

<a name="examples">

### examples

Optional

Type: [Expression](../converter/alvisnlp.corpus.expressions.Expression)

Elements to classify. This expression is evaluated as a list of elements with the corpus as the context element.

<a name="predictedClassFeatureKey">

### predictedClassFeatureKey

Optional

Type: [String](../converter/java.lang.String)

Feature where to write the class prediction.

<a name="relationDefinition">

### relationDefinition

Optional

Type: [RelationDefinition](../converter/org.bibliome.alvisnlp.modules.weka.RelationDefinition)

Specification of example attributes and class.

<a name="evaluationFile">

### evaluationFile

Optional

Type: [TargetStream](../converter/org.bibliome.util.streams.TargetStream)

File where to write evaluation results, if actual classes are available.
