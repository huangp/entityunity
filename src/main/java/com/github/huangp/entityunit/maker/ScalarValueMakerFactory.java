package com.github.huangp.entityunit.maker;

import com.github.huangp.entityunit.entity.MakeContext;
import com.github.huangp.entityunit.holder.BeanValueHolder;
import com.github.huangp.entityunit.util.ClassUtil;
import com.github.huangp.entityunit.util.Settable;
import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Transient;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * A Maker factory for field/property or constructor parameter value making.
 * <p/>
 * If MakeContext.getPreferredValueMakers().getMaker(com.github.huangp.entityunit.util.Settable) returns a matched maker,
 * it will take precedence.
 * <p/>
 * Otherwise base on the settable type, it will create different makers.
 * <pre>
 * For primitive type, a maker that uses primitive default values.
 * For String type, a maker that generates random string but respects JSR303 Size annotation and email (if applicable).
 * For Date type, a maker that returns current date.
 * For Number type and sub types, a maker that generates random integer.
 * For array, collection and map type, a maker always return null.
 * For enum type, a maker returns the first enum constant.
 * For Entity type, it will try to reuse from BeanValueHolder or null.
 * For any other type, assuming it's a bean and return a BeanMaker.
 * </pre>
 *
 * @author Patrick Huang
 * @see PreferredValueMakersRegistry
 * @see BeanValueHolder
 * @see BeanMaker
 * @see MakeContext
 */
@Slf4j
@RequiredArgsConstructor
public class ScalarValueMakerFactory {
    private static final TypeToken<Number> NUMBER_TYPE_TOKEN = TypeToken.of(Number.class);
    private final MakeContext context;

    /**
     * produce a maker for given settable
     *
     * @param settable
     *         settable
     * @return maker
     */
    public Maker from(Settable settable) {
        Optional<Maker<?>> makerOptional = context.getPreferredValueMakers().getMaker(settable);
        if (makerOptional.isPresent()) {
            return makerOptional.get();
        }

        Type type = settable.getType();
        return from(type, Optional.of(settable));
    }

    // TODO optional.absent is only used in test
    protected Maker from(Type type, Optional<Settable> optionalAnnotatedElement) {
        TypeToken<?> token = TypeToken.of(type);
        // TODO see above
        if (optionalAnnotatedElement.isPresent() && optionalAnnotatedElement.get().isAnnotationPresent(Transient.class)) {
            return new NullMaker();
        }
        if (token.getRawType().isPrimitive()) {
            return new PrimitiveMaker(token.getRawType());
        }
        if (type == String.class) {
            return StringMaker.from(optionalAnnotatedElement);
        }
        if (type == Date.class) {
            return new DateMaker();
        }
        if (NUMBER_TYPE_TOKEN.isAssignableFrom(type)) {
            return NumberMaker.from(optionalAnnotatedElement);
        }
        if (token.isArray()) {
            log.debug("array type: {}", token.getComponentType());
            return new NullMaker();
        }
        if (token.getRawType().isEnum()) {
            log.debug("enum type: {}", type);
            return new EnumMaker(token.getRawType().getEnumConstants());
        }
        if (ClassUtil.isCollection(type)) {
            log.debug("collection: {}", token);
            return new NullMaker();
        }
        if (ClassUtil.isMap(type)) {
            log.debug("map: {}", token);
            return new NullMaker<Object>();
        }
        if (ClassUtil.isEntity(type)) {
            log.debug("{} is entity type", token);
            // we don't want to make unnecessary entities
            // @see EntityMakerBuilder
            return new ReuseOrNullMaker(context.getBeanValueHolder(), token.getRawType());
        }
        log.debug("guessing this is a bean {}", token);
        return new BeanMaker(token.getRawType(), context);
    }

}
