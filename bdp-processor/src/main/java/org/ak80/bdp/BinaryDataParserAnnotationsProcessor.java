package org.ak80.bdp;

import com.google.auto.service.AutoService;
import org.ak80.bdp.annotations.MappedByte;
import org.ak80.bdp.annotations.MappedFlag;
import org.ak80.bdp.annotations.MappedWord;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This is the AnnotationsProcessor for the custom annotations of BinaryDataParser
 */
@AutoService(Processor.class)
public class BinaryDataParserAnnotationsProcessor extends AbstractProcessor {

  FileWriter bdpWriter = new BdpFileWriter();
  Generator bdpGenerator = new BdpGenerator(bdpWriter);
  BdpProcessor bdpProcessor = new CoreProcessor(new MappedClasses(), bdpGenerator);

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    this.bdpProcessor.init(processingEnv);
    this.bdpWriter.init(processingEnv.getFiler());
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    return bdpProcessor.process(annotations, roundEnv);
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> annotations = new LinkedHashSet<String>();
    annotations.add(MappedByte.class.getCanonicalName());
    annotations.add(MappedWord.class.getCanonicalName());
    annotations.add(MappedFlag.class.getCanonicalName());
    return annotations;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.RELEASE_8;
  }

}
