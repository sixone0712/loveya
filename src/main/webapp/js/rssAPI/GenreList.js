import services from "../services";

const setGenreList =  async (props)  => {
    let data = await services.axiosAPI.post("setGenre", props.genreLists.toJS());
};

export const getGenreList = (props) => {
    const { genreLists } = props;
    return genreLists.toJS();
};

export const getGenreCnt = (props) => {
    const { genreCnt } = props;
    return genreCnt.toJS();
};

const addGenreList = (props, genreList) => {
    const { genreListActions } = props;
    const { dispName, keyName, machine, fileCat } = genreList;
    genreListActions.genreAddList({dispName, keyName, machine, fileCat});
    setGenreList(props);
};

const deleteGenreList = (props, keyName) => {
    const { genreListActions } = props;
    genreListActions.genreDeleteList({ keyName });
    setGenreList(props);
};

const editGenreList = (props, genreList ) => {
    const { genreListActions } = props;
    const { dispName, keyName, machine, fileCat } = genreList;
    genreListActions.genreEditList({dispName, keyName, machine, fileCat});
    setGenreList(props);
};

const selectGenreList = (props, keyName) => {
    const { viewListActions } = this.props;
    const { genreLists } = this.props;
    console.log("genreLists", genreLists.toJS());

    const selectedGenre =  genreLists.filter(list => list.keyName === keyName);

    viewListActions.viewApplyGenreList({ genreList: selectedGenre});
};