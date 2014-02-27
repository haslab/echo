package pt.uminho.haslab.echo.engine.kodkod;

import kodkod.ast.Relation;
import kodkod.instance.Tuple;
import kodkod.instance.TupleSet;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.xtext.resource.XtextResourceSet;
import pt.uminho.haslab.echo.EchoOptionsSetup;
import pt.uminho.haslab.echo.engine.kodkod.viewer.*;
import pt.uminho.haslab.echo.util.NamingHelper;

import java.io.IOException;
import java.util.*;

/**
 * Created by tmg on 2/25/14.
 *
 */
public class InstanceViewer {
    /**result*/
    private alloy res;
    private Instance instance;

    /** all content*/
    private final Map<Relation,TupleSet> mapRelations;

    private final Map<EClass,Integer> mapClassId;

    private final Map<Object, String> mapObjectLabel;

    private final MetaInfo metaInfo;
    private final ViewerFactory factory;
    private int idCounter;



    public static void main(String args[]){
        InstanceViewer iv = new InstanceViewer(null,null);
        save(iv.getAlloyInstance());
    }


    public InstanceViewer(Map<Relation,TupleSet> map, Set<EKodkodMetamodel> metas){
        mapRelations = map;
        this.metaInfo = new MetaInfo(metas);
        mapObjectLabel = new HashMap<>();
        mapClassId = new HashMap<>();
        factory = ViewerFactory.eINSTANCE;

        makeBasic();

        Set<EClass> abs = metaInfo.getAbstracts();
        Set<Relation> refs = new HashSet<>();
        Set<Relation> bools = new HashSet<>();
        Set<Relation> rels = new HashSet<>();

        for(EClass ec : abs)
            handleAbs(ec);




        for(Relation rel : mapRelations.keySet())
        {
            if(rel.arity() == 2)
                refs.add(rel);
            else if(metaInfo.isBoolRel(rel))
                bools.add(rel);
            else if(rel != KodkodUtil.stringRel)
                rels.add(rel);
        }


        for(Relation rel: rels)
            handleRel(rel);
        for(Relation rel: refs)
            handleRef(rel);
        for(Relation rel: bools)
            handleBool(rel);



    }




    private void handleAbs(EClass ec) {
        if(!mapClassId.containsKey(ec)){
            Sig s;

            String className = ec.getName();
            int id =  idCounter++;
            mapClassId.put(ec,id);
            EList<EClass> list = ec.getESuperTypes();
            if(list.size()>0)
            {
                EClass parent = list.get(0);
                Relation p = metaInfo.getRelation(parent);
                if(p == null)
                    handleAbs(parent);
                else
                    handleRel(p);
                 s = factory.createSig(id,mapClassId.get(parent),className);
            }else
                s = factory.createSig(id,className);

            s.setAbstract("yes");
            instance.getSig().add(s);
        }
    }


    private void makeBasic() {
        res = factory.createalloy();
        instance = factory.createInstance();

        instance.setBitwidth(EchoOptionsSetup.getInstance().getBitwidth());
        instance.setMaxseq(instance.getBitwidth()+1);


        res.getInstance().add(instance);


        Sig univ = factory.createSig();
        univ.setID(2);
        univ.setLabel("univ");
        univ.setBuiltin("yes");

        Sig intSig = factory.createSig(1,2,"Int","yes");
        Sig seqInt = factory.createSig(0,1,"seq/Int","yes");
        Sig string = factory.createSig(3,2,"String","yes");

        instance.getSig().add(univ);
        instance.getSig().add(intSig);
        instance.getSig().add(seqInt);
        instance.getSig().add(string);

        idCounter = 4;

        TupleSet ts = mapRelations.get(KodkodUtil.stringRel);
        for(Tuple t: ts){
            Atom a = factory.createAtom();
            a.setLabel(((String)t.atom(0)).substring(3));
            string.getAtom().add(a);
        }

    }

    private void handleRel(Relation rel) {
        //TODO getEclass might return enum
        EClass ec =(EClass) metaInfo.getEClass(rel);
        if(!mapClassId.containsKey(ec)){
            Sig s;
            String className = ec.getName();
            int id =  idCounter++;
            mapClassId.put(ec,id);
            EList<EClass> list = ec.getESuperTypes();
            if(list.size()>0)
            {
                EClass parent = list.get(0);
                Relation p = metaInfo.getRelation(parent);
                if(p == null)
                    handleAbs(parent);
                else
                    handleRel(p);
                s = factory.createSig(id,mapClassId.get(parent),className);
            }else
                s = factory.createSig(id,className);
            instance.getSig().add(s);

            TupleSet ts = mapRelations.get(rel);
            for(Tuple t: ts )
            {
                String atomLabel = NamingHelper.nameAtom(ec);
                mapObjectLabel.put(t.atom(0),atomLabel);
                Atom atom = factory.createAtom();
                atom.setLabel(atomLabel);
                s.getAtom().add(atom);
            }
        }


    }

    private void handleBool(Relation rel) {
        EStructuralFeature sf = metaInfo.getSf(rel);
        int id = idCounter++;
        EClass parent = sf.getEContainingClass();
        int parentId = mapClassId.get(parent);

        Sig s = factory.createSig(id,sf.getName());
        Type type = factory.createType();
        type.setID(parentId);
        s.getType().add(type);

        TupleSet ts = mapRelations.get(rel);
        for(Tuple t: ts )
              s.getAtom().add(makeAtom(t.atom(0)));

        instance.getSig().add(s);


    }


    private void handleRef(Relation rel) {
        EStructuralFeature sf = metaInfo.getSf(rel);
        int id = idCounter++;
        EClass parent = sf.getEContainingClass();
        int parentId = mapClassId.get(parent);
        

        Field field = factory.createField();
        field.setID(id);
        field.setParentID(parentId);
        field.setLabel(NamingHelper.nameField(sf));

        int typeId=0; //TODO might be ENUM
        if(sf instanceof EReference)
            typeId = mapClassId.get((EClass) sf.getEType());
        else if(sf instanceof EAttribute)
        {
            if(sf.getEType().getName().equals("EString"))
                typeId = 3;
            else if(sf.getEType().getName().equals("EInt"))
                typeId = 1;
            else
                System.out.println("ASDASDASDASDASDASDASDSAD");
        }else
        	System.out.println("dsdddddddd" + sf.getName());

        System.out.println(typeId);
        
        
        Types types = factory.createTypes();
        EList<Type> lTypes = types.getType();
        Type type = factory.createType();
        type.setID(parentId);

        Type type2 = factory.createType();
        type2.setID(typeId);


        lTypes.add(type);
        lTypes.add(type2);
        field.getTypes().add(types);


        EList<AlloyTuple> tuples = field.getTuple();
        TupleSet ts = mapRelations.get(rel);
        for(Tuple t: ts )
        {
            AlloyTuple at = factory.createAlloyTuple();
            at.getAtom().add(makeAtom(t.atom(0)));
            at.getAtom().add(makeAtom(t.atom(1)));
            tuples.add(at);
        }

        instance.getField().add(field);
    }

    private Atom makeAtom(Object obj){
        String label;
        Atom res = factory.createAtom();
        if(obj instanceof String){
            String str = (String) obj;
            if(str.startsWith("str"))
                label = str.substring(3);
            else
                label = str;
        }
        else
            label = mapObjectLabel.get(obj);
        res.setLabel(label);
        return res;
    }

    public alloy getAlloyInstance(){
        return res;
    }


    static String save(EObject obj) {
        XtextResourceSet resourceSet = new XtextResourceSet();

        resourceSet.getResourceFactoryRegistry()
                .getExtensionToFactoryMap().put("xml",
                new XMLResourceFactoryImpl());

        XMLResource resource = (XMLResource) resourceSet.createResource(URI.createURI("alloyinstance.xml"));
        resource.getContents().add(obj);

        Map<Object,Object> options = new HashMap<>();
        options.put(XMLResource.OPTION_SCHEMA_LOCATION, true);
        try{
            resource.save(options);
        }catch (IOException e) {
            e.printStackTrace();
        }

        return "alloyinstance.xml";
    }

    private class MetaInfo {
        private Set<EKodkodMetamodel> metas;

        MetaInfo(Set<EKodkodMetamodel> metas)
        {
            this.metas =metas;
        }


        public Set<EClass> getAbstracts() {
            Set<EClass> abs = new HashSet<>();
            for(EKodkodMetamodel e2k : metas)
                abs.addAll(e2k.getAbstracts());

            return abs;
        }

        public boolean isBoolRel(Relation rel) {
            Iterator<EKodkodMetamodel> it = metas.iterator();

            boolean isBool = false;
            while (it.hasNext() && !isBool){
                EKodkodMetamodel e2k = it.next();
                if(e2k.getBoolType(rel) != null)
                    isBool = true;
            }


            return isBool;

        }

        public Relation getRelation(EClass ec) {
            Iterator<EKodkodMetamodel> it = metas.iterator();
            Relation rel = null;


            while (it.hasNext() && rel==null){
                EKodkodMetamodel e2k = it.next();
                rel = e2k.getRelation(ec);
            }


           return rel;
        }

        public EClassifier getEClass(Relation rel) {
            Iterator<EKodkodMetamodel> it = metas.iterator();
            EClassifier ec = null;


            while (it.hasNext() && ec==null){
                EKodkodMetamodel e2k = it.next();
                ec = e2k.getEClass(rel);
            }


            return ec;
        }

        public EStructuralFeature getSf(Relation rel) {
            Iterator<EKodkodMetamodel> it = metas.iterator();
            EStructuralFeature sf = null;


            while (it.hasNext() && sf==null){
                EKodkodMetamodel e2k = it.next();
                sf = e2k.getSf(rel);
            }


            return sf;
        }
    }

}
