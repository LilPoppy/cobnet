package com.cobnet.spring.boot.controller.support;

import com.cobnet.interfaces.spring.controller.BodyPart;
import com.cobnet.interfaces.spring.controller.BodyPartKey;
import com.cobnet.interfaces.spring.controller.ExpressionGenerator;
import com.cobnet.interfaces.spring.controller.ExpressionOperator;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import java.lang.reflect.Type;
import java.util.Arrays;

@Getter
@Setter
public class EntityKeyInfo implements BodyPartKey, ExpressionGenerator<Path<?>> {

    private Type type;

    private String key;

    private String[] references;

    private Object value;

    private EntityKeyPredicateInfo parent;

    public EntityKeyInfo(EntityKeyPredicateInfo parent, Type type, String key) {
        this.parent = parent;
        this.key = key;
        this.type = type;
    }

    public EntityVariableInfo getEntityVariableInfo() {

        return this.getAttributeConditionInfo().getParent();
    }

    public EntityAttributeInfo getAttributeInfo() {

        return this.getEntityKeyPredicateInfo().getParent();
    }

    public EntityAttributeConditionInfo getAttributeConditionInfo() {

        return this.getAttributeInfo().getParent();
    }

    public EntityKeyPredicateInfo getEntityKeyPredicateInfo() {

        return this.getParent();
    }

    public boolean isNullable() {

        return this.getAttributeInfo().isKeyNullable(key);
    }

    @Override
    public BodyPart getBodyPart() {

        return this.getEntityVariableInfo();
    }

    @Override
    public String toString() {
        return "EntityKeyInfo{" +
                "type=" + type +
                ", key='" + key + '\'' +
                ", referenced='" + Arrays.toString(references) + '\'' +
                ", value=" + value +
                '}';
    }

    @Override
    public Expression<?> generate(EntityManager manager, CriteriaBuilder builder, Path<?> arg) {

        //Alia is not gonna work check org.hibernate.query.criteria.internal.expression.LiteralExpression.bindLiteral()
        return new LiteralExpression<>((CriteriaBuilderImpl) builder, this.getEntityVariableInfo().getType(), this.value);
    }

    @Override
    public ExpressionOperator getOperator() {
        return null;
    }

}
