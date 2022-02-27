package org.example.sdj.advance.support.factory;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.repository.support.JdbcRepositoryFactory;
import org.springframework.data.mapping.callback.EntityCallbacks;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.mapping.RelationalMappingContext;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

/**
 * copy from JdbcRepositoryFactory, costomize method getTargetRepository
 *
 * @see JdbcRepositoryFactory
 */
public class MyJdbcRepositoryFactory extends JdbcRepositoryFactory {

    private RelationalMappingContext context;
    private JdbcConverter converter;
    private ApplicationEventPublisher publisher;
    private DataAccessStrategy accessStrategy;
    private NamedParameterJdbcOperations operations;
    private Dialect dialect;

    private EntityCallbacks entityCallbacks;

    /**
     * Creates a new {@link JdbcRepositoryFactory} for the given {@link DataAccessStrategy},
     * {@link RelationalMappingContext} and {@link ApplicationEventPublisher}.
     *
     * @param dataAccessStrategy must not be {@literal null}.
     * @param context            must not be {@literal null}.
     * @param converter          must not be {@literal null}.
     * @param dialect            must not be {@literal null}.
     * @param publisher          must not be {@literal null}.
     * @param operations         must not be {@literal null}.
     */
    public MyJdbcRepositoryFactory(DataAccessStrategy dataAccessStrategy, RelationalMappingContext context, JdbcConverter converter, Dialect dialect, ApplicationEventPublisher publisher, NamedParameterJdbcOperations operations) {
        super(dataAccessStrategy, context, converter, dialect, publisher, operations);
        this.accessStrategy = dataAccessStrategy;
        this.context = context;
        this.converter = converter;
        this.publisher = publisher;
        this.operations = operations;
        this.dialect = dialect;
    }


    @Override
    public void setEntityCallbacks(EntityCallbacks entityCallbacks) {
        this.entityCallbacks = entityCallbacks;
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation repositoryInformation) {
        JdbcAggregateTemplate template = new JdbcAggregateTemplate(publisher, context, converter, accessStrategy);

        if (entityCallbacks != null) {
            template.setEntityCallbacks(entityCallbacks);
        }

        RelationalPersistentEntity<?> persistentEntity = context
                .getRequiredPersistentEntity(repositoryInformation.getDomainType());

        return instantiateClass(repositoryInformation.getRepositoryBaseClass(), template, persistentEntity, this.operations, this.dialect, this.converter);
    }
}
